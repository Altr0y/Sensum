package si.sensum.backend.service

import si.sensum.backend.client.GmClient
import si.sensum.backend.repository.MeasurementRepository
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand
import si.sensum.shared.models.measurements.RefreshMeasurementsResult

class MeasurementRefreshService(
    private val gmClient: GmClient,
    private val measurementRepository: MeasurementRepository,
    private val swsUsername: String,
    private val swsPassword: String
) {
    suspend fun refresh(
        request: RefreshMeasurementsCommand
    ): RefreshMeasurementsResult {
        require(swsUsername.isNotBlank()) { "Missing SWS_USERNAME" }
        require(swsPassword.isNotBlank()) { "Missing SWS_PASSWORD" }

        val normalizedRequest = request.copy(
            datetimeFrom = ApiDateTime.requireNormalizedLocal("datetimeFrom", request.datetimeFrom),
            datetimeTo = ApiDateTime.requireNormalizedLocal("datetimeTo", request.datetimeTo)
        )

        val gmLogin = gmClient.login(swsUsername, swsPassword)

        val measurements = gmClient.getMeasurements(
            gmSessionToken = gmLogin.token,
            request = normalizedRequest
        )

        val (deleted, inserted) = measurementRepository.replaceAllFromDtos(measurements)

        return RefreshMeasurementsResult(
            deletedCount = deleted,
            insertedCount = inserted
        )
    }
}