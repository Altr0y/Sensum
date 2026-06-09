package si.sensum.backend.service

import si.sensum.backend.client.GmClient
import si.sensum.backend.repository.MeasurementRepository
import si.sensum.shared.models.common.DataSourceDto
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
    ): RefreshMeasurementsResult = ServiceLogger.suspendCall(
        service = "measurement-refresh",
        operation = "refresh",
        details = "pairs=${request.stationChannelPairs.size} from=${request.datetimeFrom} to=${request.datetimeTo}"
    ) {
        require(swsUsername.isNotBlank()) {
            "Missing SWS_USERNAME"
        }

        require(swsPassword.isNotBlank()) {
            "Missing SWS_PASSWORD"
        }

        require(request.stationChannelPairs.isNotEmpty()) {
            "Missing required field: stationChannelPairs"
        }

        val normalizedRequest = request.copy(
            datetimeFrom = ApiDateTime.requireNormalizedLocal(
                fieldName = "datetimeFrom",
                value = request.datetimeFrom
            ),
            datetimeTo = ApiDateTime.requireNormalizedLocal(
                fieldName = "datetimeTo",
                value = request.datetimeTo
            )
        )

        val gmLogin = gmClient.login(
            username = swsUsername,
            password = swsPassword
        )

        val measurements = gmClient.getMeasurements(
            gmSessionToken = gmLogin.token,
            request = normalizedRequest
        )

        val (deleted, inserted) = measurementRepository.replaceAllFromDtos(
            measurements = measurements,
            source = DataSourceDto.SWS
        )

        RefreshMeasurementsResult(
            deletedCount = deleted,
            insertedCount = inserted
        )
    }
}