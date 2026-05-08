package si.sensum.demo.repository

import si.sensum.demo.model.Measurement
import si.sensum.demo.model.MeasurementRequest

class MockMeasurementRepository : MeasurementRepository {

    override suspend fun getMeasurements(request: MeasurementRequest): Result<List<Measurement>> {
        // Simulira zamudo API klica
        kotlinx.coroutines.delay(500)

        val measurements = mutableListOf<Measurement>()
        var time = request.datetimeFrom

        for (pair in request.pairs) {
            while (time.isBefore(request.datetimeTo)) {
                measurements.add(
                    Measurement(
                        stationId = pair.stationId,
                        stationName = "Radar test",
                        channelId = pair.channelId,
                        channelName = mockChannelName(pair.channelId),
                        dateTime = time,
                        value = (2.5 + Math.random() * 0.5),
                        status = 0
                    )
                )
                time = time.plusMinutes(10)
            }
            time = request.datetimeFrom
        }

        return Result.success(measurements)
    }

    private fun mockChannelName(channelId: Int) = when (channelId) {
        127 -> "L8001H - globina voda-radar [m]"
        128 -> "L8001H - višina vode [m]"
        129 -> "L8001H - globina vodnjaka (PPI220) [m]"
        130 -> "PPI220 - Nivo [m]"
        131 -> "PPI220 - Temperatura [°C]"
        132 -> "PPI220 - globina vode-nivo [m]"
        133 -> "L8001H - globina vode-nivo [-]"
        134 -> "L8001H - Nivo [-]"
        135 -> "L8001H - 4 [-]"
        else -> "Channel $channelId"
    }
}
