package si.sensum.backend.measurements

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import si.sensum.simulator.SimulatorService
import java.time.LocalDateTime as JavaLocalDateTime
import kotlinx.datetime.number

class MeasurementService(
    private val repository: MeasurementRepository = MeasurementRepository(),
    private val simulator: SimulatorService = SimulatorService()
) {

    fun getMeasurements(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Measurement> {
        return if (simulator.isRealDataAvailable(from.toJava(), to.toJava())) {
            repository.findByChannelAndRange(channelId, from, to)
        } else {
            getOrGenerateMeasurements(channelId, from, to)
        }
    }

    private fun getOrGenerateMeasurements(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Measurement> {
        val existing = repository.findByChannelAndRange(channelId, from, to)
        if (existing.isNotEmpty()) return existing

        val generated = simulator.generateMeasurements(from.toJava(), to.toJava())

        val toInsert = generated.map { m ->
            Measurement(
                id = 0,
                channelId = m.channelId,
                dateTime = m.dateTime.toKotlinLocalDateTime(),
                value = m.value.toFloat(),
                status = false
            )
        }

        repository.batchInsert(toInsert)

        return toInsert.filter { it.channelId == channelId }
    }

    private fun LocalDateTime.toJava(): JavaLocalDateTime =
        JavaLocalDateTime.of(year, month.number, day, hour, minute, second)
}