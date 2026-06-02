package si.sensum.demo.repository

import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.model.MeasurementRequestUi
import si.sensum.demo.model.MeasurementUi
import si.sensum.shared.models.measurements.StationChannelPairDto
import java.time.format.DateTimeFormatter

class ApiMeasurementRepository(
    private val apiClient: SensumApiClient
) : MeasurementRepository {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override suspend fun getMeasurements(
        request: MeasurementRequestUi
    ): Result<List<MeasurementUi>> {
        return runCatching {
            request.pairs.flatMap { pair ->
                apiClient.getMeasurementsByRange(
                    channelId = pair.channelId,
                    datetimeFrom = request.datetimeFrom.format(formatter),
                    datetimeTo = request.datetimeTo.format(formatter)
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
                datetimeFrom = request.datetimeFrom.format(formatter),
                datetimeTo = request.datetimeTo.format(formatter)
            )

            request.pairs.flatMap { pair ->
                apiClient.getMeasurementsByRange(
                    channelId = pair.channelId,
                    datetimeFrom = request.datetimeFrom.format(formatter),
                    datetimeTo = request.datetimeTo.format(formatter)
                )
            }
        }
    }
}