package si.sensum.backend.service

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.datetime.toKotlinLocalDateTime
import si.sensum.backend.domain.measurement.MeasurementEntity
import si.sensum.backend.repository.MeasurementRepository
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.simulator.SimulatorService

class MeasurementService(
    private val repository: MeasurementRepository,
    private val simulator: SimulatorService = SimulatorService()
) {
    fun getAllMeasurements(): List<MeasurementDto> {
        return repository.findAll()
    }

    fun getMeasurements(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<MeasurementDto> {
        return if (simulator.isRealDataAvailable(from.toJava(), to.toJava())) {
            repository.findByChannelAndRange(channelId, from, to)
        } else {
            getOrGenerateMeasurements(channelId, from, to)
        }
    }

    fun createMeasurement(
        request: MeasurementDto
    ): MeasurementDto {
        return repository.create(request)
    }

    fun createMeasurementsBatch(
        measurements: List<MeasurementDto>
    ): Int {
        require(measurements.isNotEmpty()) {
            "Measurement batch must not be empty"
        }

        return repository.createBatch(measurements)
    }

    fun updateMeasurement(
        measurementId: Long,
        request: MeasurementDto
    ): MeasurementDto {
        return repository.update(
            id = measurementId,
            dto = request
        ) ?: error("Measurement not found")
    }

    fun deleteMeasurement(
        measurementId: Long
    ) {
        val deleted = repository.delete(measurementId)

        if (!deleted) {
            error("Measurement not found")
        }
    }

    fun deleteAllMeasurements(): Int {
        return repository.deleteAll()
    }

    fun deleteMeasurementsByRange(
        from: LocalDateTime,
        to: LocalDateTime
    ) {
        repository.deleteByRange(from, to)
    }

    fun regenerateMeasurements(
        from: LocalDateTime,
        to: LocalDateTime
    ) {
        repository.deleteByRange(from, to)

        val generated = simulateAndInsert(
            from = from,
            to = to
        )

        repository.batchInsert(generated)
    }

    fun clearSimulated(
        from: LocalDateTime,
        to: LocalDateTime
    ) {
        repository.deleteByRange(from, to)
    }

    private fun getOrGenerateMeasurements(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<MeasurementDto> {
        if (!repository.existsInRange(from, to)) {
            val generated = simulateAndInsert(
                from = from,
                to = to
            )

            repository.batchInsert(generated)
        }

        return repository.findByChannelAndRange(channelId, from, to)
    }

    private fun simulateAndInsert(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<MeasurementEntity> {
        return simulator
            .generateMeasurements(from.toJava(), to.toJava())
            .map { measurement ->
                MeasurementEntity(
                    id = 0,
                    channelId = measurement.channelId,
                    dateTime = measurement.dateTime.toKotlinLocalDateTime(),
                    value = measurement.value.toFloat(),
                    status = false
                )
            }
    }

    private fun LocalDateTime.toJava(): java.time.LocalDateTime {
        return java.time.LocalDateTime.of(
            year,
            month.number,
            day,
            hour,
            minute,
            second
        )
    }
}