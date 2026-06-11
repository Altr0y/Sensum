package si.sensum.demo.repository

import si.sensum.demo.model.MeasurementRequestUi
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.util.DateTimeFormat.toApiString
import si.sensum.shared.models.measurements.StationChannelPairDto

class ApiMeasurementRepository(
    private val apiClient: SensumApiClient
) : MeasurementRepository {

    override suspend fun getMeasurements(
        request: MeasurementRequestUi
    ): Result<List<MeasurementUi>> {
        return runCatching {
            request.pairs.flatMap { pair ->
                apiClient.getMeasurementsByRange(
                    channelId = pair.channelId,
                    datetimeFrom = request.datetimeFrom.toApiString(),
                    datetimeTo = request.datetimeTo.toApiString()
                )
            }
        }
    }

    suspend fun refreshAndGet(
        request: MeasurementRequestUi
    ): Result<List<MeasurementUi>> {
        return runCatching {
            apiClient.refreshMeasurements(
                stationChannelPairs = request.pairs.map { pair ->
                    StationChannelPairDto(
                        stationId = pair.stationId.toLong(),
                        channelId = pair.channelId
                    )
                },
                datetimeFrom = request.datetimeFrom.toApiString(),
                datetimeTo = request.datetimeTo.toApiString()
            )

            request.pairs.flatMap { pair ->
                apiClient.getMeasurementsByRange(
                    channelId = pair.channelId,
                    datetimeFrom = request.datetimeFrom.toApiString(),
                    datetimeTo = request.datetimeTo.toApiString()
                )
            }
        }
    }
}