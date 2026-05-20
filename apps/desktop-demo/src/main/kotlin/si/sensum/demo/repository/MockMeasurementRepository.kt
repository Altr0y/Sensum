package si.sensum.demo.repository

import kotlinx.coroutines.delay
import si.sensum.demo.model.DemoChannels
import si.sensum.demo.model.Measurement
import si.sensum.demo.model.MeasurementRequest
import java.time.LocalDateTime

class MockMeasurementRepository : MeasurementRepository {

    override suspend fun getMeasurements(request: MeasurementRequest): Result<List<Measurement>> {
        delay(500)

        return Result.success(
            request.pairs.flatMap { pair ->
                generateMeasurementsForPair(
                    stationId = pair.stationId,
                    channelId = pair.channelId,
                    datetimeFrom = request.datetimeFrom,
                    datetimeTo = request.datetimeTo
                )
            }
        )
    }

    private fun generateMeasurementsForPair(
        stationId: Int,
        channelId: Int,
        datetimeFrom: LocalDateTime,
        datetimeTo: LocalDateTime
    ): List<Measurement> {
        return generateSequence(datetimeFrom) { time ->
            time.plusMinutes(10).takeIf { it.isBefore(datetimeTo) }
        }.map { time ->
            Measurement(
                stationId = stationId,
                stationName = DemoChannels.DEFAULT_STATION_NAME,
                channelId = channelId,
                channelName = DemoChannels.nameOf(channelId),
                dateTime = time,
                value = 2.5 + Math.random() * 0.5,
                status = 0
            )
        }.toList()
    }
}