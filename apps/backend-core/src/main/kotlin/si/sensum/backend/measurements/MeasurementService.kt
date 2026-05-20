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

    fun regenerateMeasurements(from: LocalDateTime, to: LocalDateTime) {
        repository.deleteByRange(from, to)
        val generated = simulateAndInsert(from, to)
        repository.batchInsert(generated)
    }

    private fun getOrGenerateMeasurements(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Measurement> {
        val existing = repository.findByChannelAndRange(channelId, from, to)

        val expectedHours = java.time.Duration.between(from.toJava(), to.toJava()).toHours()
        val actualCount = existing.size.toLong()

        if (actualCount >= expectedHours) return existing

        repository.deleteByRange(from, to)
        val generated = simulateAndInsert(from, to)
        repository.batchInsert(generated)
        return generated.filter { it.channelId == channelId }
    }

    private fun simulateAndInsert(from: LocalDateTime, to: LocalDateTime): List<Measurement> {
        return simulator.generateMeasurements(from.toJava(), to.toJava()).map { m ->
            Measurement(
                id = 0,
                channelId = m.channelId,
                dateTime = m.dateTime.toKotlinLocalDateTime(),
                value = m.value.toFloat(),
                status = false
            )
        }
    }

    fun clearSimulated(from: LocalDateTime, to: LocalDateTime) {
        repository.deleteByRange(from, to)
    }

    private fun LocalDateTime.toJava(): JavaLocalDateTime =
        JavaLocalDateTime.of(year, month.number, day, hour, minute, second)
}