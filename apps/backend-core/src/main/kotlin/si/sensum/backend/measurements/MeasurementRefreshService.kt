package si.sensum.backend.measurements

import si.sensum.backend.gm.GmClient
import si.sensum.shared.models.api.measurements.RefreshMeasurementsRequest
import si.sensum.shared.models.api.measurements.RefreshMeasurementsResponse

class MeasurementRefreshService(
    private val gmClient: GmClient,
    private val measurementRepository: MeasurementRepository,
    private val swsUsername: String,
    private val swsPassword: String
) {
    suspend fun refresh(
        request: RefreshMeasurementsRequest
    ): RefreshMeasurementsResponse {
        require(swsUsername.isNotBlank()) { "Missing SWS_USERNAME" }
        require(swsPassword.isNotBlank()) { "Missing SWS_PASSWORD" }

        val gmLogin = gmClient.login(swsUsername, swsPassword)

        val measurements = gmClient.getMeasurements(
            gmSessionToken = gmLogin.token,
            request = request
        )

        val (deleted, inserted) = measurementRepository.replaceAllFromDtos(measurements)

        return RefreshMeasurementsResponse(
            deletedCount = deleted,
            insertedCount = inserted
        )
    }
}