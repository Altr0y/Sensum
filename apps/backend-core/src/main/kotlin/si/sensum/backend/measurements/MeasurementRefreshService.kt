package si.sensum.backend.measurements

import si.sensum.backend.gm.GmClient
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsResponse
import si.sensum.shared.models.datetime.ApiDateTime

class MeasurementRefreshService(
    private val gmClient: GmClient,
    private val measurementRepository: MeasurementRepository,
    private val swsUsername: String,
    private val swsPassword: String
) {
    suspend fun refresh(
        request: MeasurementsByStationChannelPairsRequest
    ): MeasurementsByStationChannelPairsResponse {
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

        return MeasurementsByStationChannelPairsResponse(
            deletedCount = deleted,
            insertedCount = inserted
        )
    }
}