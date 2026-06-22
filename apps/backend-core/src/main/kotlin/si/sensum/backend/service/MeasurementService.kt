package si.sensum.backend.service

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.datetime.toKotlinLocalDateTime
import si.sensum.backend.domain.measurement.MeasurementEntity
import si.sensum.backend.repository.MeasurementRepository
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.simulator.SimulatorService as MeasurementSimulatorService
import si.sensum.backend.repository.ChannelRepository
import si.sensum.shared.models.common.DataSourceDto

class MeasurementService(
    private val repository: MeasurementRepository,
    private val channelRepository: ChannelRepository,
    private val simulator: MeasurementSimulatorService = MeasurementSimulatorService()
) {
    fun getAllMeasurements(): List<MeasurementDto> = ServiceLogger.call(
        service = "measurement",
        operation = "getAllMeasurements"
    ) {
        repository.findAll()
    }

    fun getMeasurements(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<MeasurementDto> = ServiceLogger.call(
        service = "measurement",
        operation = "getMeasurements",
        details = "channelId=$channelId from=$from to=$to"
    ) {
        if (isProtectedFromSimulation(channelId) || simulator.isRealDataAvailable(from.toJava(), to.toJava())) {
            repository.findByChannelAndRange(channelId, from, to)
        } else {
            getOrGenerateMeasurements(channelId, from, to)
        }
    }

    private fun isProtectedFromSimulation(channelId: Int): Boolean {
        val channel = channelRepository.findById(channelId) ?: return false
        return channel.source == DataSourceDto.SWS
    }

    fun createMeasurement(
        request: MeasurementDto
    ): MeasurementDto = ServiceLogger.call(
        service = "measurement",
        operation = "createMeasurement",
        details = "channelId=${request.channelId}"
    ) {
        repository.create(
            request.copy(
                source = normalizeSource(
                    source = request.source,
                    fallback = DataSourceDto.MANUAL
                )
            )
        )
    }

    fun createMeasurementsBatch(
        measurements: List<MeasurementDto>
    ): Int = ServiceLogger.call(
        service = "measurement",
        operation = "createMeasurementsBatch",
        details = "count=${measurements.size}"
    ) {
        require(measurements.isNotEmpty()) {
            "Measurement batch must not be empty"
        }

        repository.createBatch(
            measurements.map { measurement ->
                measurement.copy(
                    source = normalizeSource(
                        source = measurement.source,
                        fallback = DataSourceDto.MANUAL
                    )
                )
            }
        )
    }

    fun updateMeasurement(
        measurementId: Long,
        request: MeasurementDto
    ): MeasurementDto = ServiceLogger.call(
        service = "measurement",
        operation = "updateMeasurement",
        details = "measurementId=$measurementId"
    ) {
        repository.update(
            id = measurementId,
            dto = request.copy(
                source = normalizeSource(
                    source = request.source,
                    fallback = DataSourceDto.MANUAL
                )
            )
        ) ?: error("Measurement not found")
    }

    fun deleteMeasurement(
        measurementId: Long
    ) = ServiceLogger.call(
        service = "measurement",
        operation = "deleteMeasurement",
        details = "measurementId=$measurementId"
    ) {
        val deleted = repository.delete(measurementId)

        if (!deleted) {
            error("Measurement not found")
        }
    }

    fun deleteAllMeasurements(): Int = ServiceLogger.call(
        service = "measurement",
        operation = "deleteAllMeasurements"
    ) {
        repository.deleteAll()
    }

    fun deleteMeasurementsByRange(
        from: LocalDateTime,
        to: LocalDateTime
    ) = ServiceLogger.call(
        service = "measurement",
        operation = "deleteMeasurementsByRange",
        details = "from=$from to=$to"
    ) {
        repository.deleteByRange(from, to)
    }

    fun regenerateMeasurements(
        from: LocalDateTime,
        to: LocalDateTime
    ) = ServiceLogger.call(
        service = "measurement",
        operation = "regenerateMeasurements",
        details = "from=$from to=$to"
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
    ) = ServiceLogger.call(
        service = "measurement",
        operation = "clearSimulated",
        details = "from=$from to=$to"
    ) {
        repository.deleteByRange(from, to)
    }

    private fun getOrGenerateMeasurements(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<MeasurementDto> {
        if (!repository.existsInRange(channelId, from, to)) {
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
                    status = false,
                    source = DataSourceDto.SIM
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

    private fun normalizeSource(
        source: DataSourceDto,
        fallback: DataSourceDto
    ): DataSourceDto {
        return if (source == DataSourceDto.UNKNOWN) {
            fallback
        } else {
            source
        }
    }
}