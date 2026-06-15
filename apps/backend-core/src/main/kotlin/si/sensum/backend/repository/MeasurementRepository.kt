package si.sensum.backend.repository

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import si.sensum.backend.database.ChannelTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.MeasurementTable
import si.sensum.backend.domain.measurement.MeasurementEntity
import si.sensum.backend.mapper.toDataSourceDto
import si.sensum.backend.mapper.toDbValue
import si.sensum.shared.models.common.DataSourceDto
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.shared.models.measurements.MeasurementDto
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MeasurementRepository {

    fun findAll(): List<MeasurementDto> = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
            .orderBy(
                MeasurementTable.channelId to SortOrder.ASC,
                MeasurementTable.dateTime to SortOrder.ASC
            )
            .map(::toDto)
    }

    fun findByChannelAndRange(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<MeasurementDto> = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
            .where {
                (MeasurementTable.channelId eq channelId) and
                        (MeasurementTable.dateTime greaterEq from) and
                        (MeasurementTable.dateTime lessEq to)
            }
            .orderBy(MeasurementTable.dateTime to SortOrder.ASC)
            .map(::toDto)
    }

    fun create(dto: MeasurementDto): MeasurementDto = DatabaseTransaction.run {
        val id = MeasurementTable.insert {
            it[channelId] = dto.channelId
            it[dateTime] = dto.toDbDateTime()
            it[value] = dto.value.toFloat()
            it[status] = dto.status != 0
            it[dataSource] = dto.source.toDbValue()
        } get MeasurementTable.id

        MeasurementTable
            .selectAll()
            .where { MeasurementTable.id eq id }
            .map(::toDto)
            .single()
    }

    fun createBatch(measurements: List<MeasurementDto>): Int = DatabaseTransaction.run {
        if (measurements.isEmpty()) {
            return@run 0
        }

        MeasurementTable.batchInsert(
            data = measurements,
            shouldReturnGeneratedValues = false
        ) { measurement ->
            this[MeasurementTable.channelId] = measurement.channelId
            this[MeasurementTable.dateTime] = measurement.toDbDateTime()
            this[MeasurementTable.value] = measurement.value.toFloat()
            this[MeasurementTable.status] = measurement.status != 0
            this[MeasurementTable.dataSource] = measurement.source.toDbValue()
        }

        measurements.size
    }

    fun batchInsert(measurements: List<MeasurementEntity>): Int = DatabaseTransaction.run {
        if (measurements.isEmpty()) {
            return@run 0
        }

        MeasurementTable.batchInsert(
            data = measurements,
            shouldReturnGeneratedValues = false
        ) { measurement ->
            this[MeasurementTable.channelId] = measurement.channelId
            this[MeasurementTable.dateTime] = measurement.dateTime
            this[MeasurementTable.value] = measurement.value
            this[MeasurementTable.status] = measurement.status
            this[MeasurementTable.dataSource] = measurement.source.toDbValue()
        }

        measurements.size
    }

    fun update(
        id: Long,
        dto: MeasurementDto
    ): MeasurementDto? = DatabaseTransaction.run {
        val updatedCount = MeasurementTable.update({ MeasurementTable.id eq id }) {
            it[channelId] = dto.channelId
            it[dateTime] = dto.toDbDateTime()
            it[value] = dto.value.toFloat()
            it[status] = dto.status != 0
            it[dataSource] = dto.source.toDbValue()
        }

        if (updatedCount == 0) {
            null
        } else {
            MeasurementTable
                .selectAll()
                .where { MeasurementTable.id eq id }
                .map(::toDto)
                .singleOrNull()
        }
    }

    fun delete(id: Long): Boolean = DatabaseTransaction.run {
        MeasurementTable.deleteWhere {
            MeasurementTable.id eq id
        } > 0
    }

    fun deleteAll(): Int = DatabaseTransaction.run {
        MeasurementTable.deleteAll()
    }

    fun deleteByRange(
        from: LocalDateTime,
        to: LocalDateTime
    ) {
        DatabaseTransaction.run {
            MeasurementTable.deleteWhere {
                (MeasurementTable.dateTime greaterEq from) and
                        (MeasurementTable.dateTime lessEq to)
            }
        }
    }

    fun deleteByChannelAndRange(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ) {
        DatabaseTransaction.run {
            MeasurementTable.deleteWhere {
                (MeasurementTable.channelId eq channelId) and
                        (MeasurementTable.dateTime greaterEq from) and
                        (MeasurementTable.dateTime lessEq to)
            }
        }
    }

    fun replaceAllFromDtos(
        measurements: List<MeasurementDto>,
        source: DataSourceDto
    ): Pair<Int, Int> = DatabaseTransaction.run {
        val deleted = MeasurementTable.deleteAll()

        MeasurementTable.batchInsert(
            data = measurements,
            shouldReturnGeneratedValues = false
        ) { dto ->
            this[MeasurementTable.channelId] = dto.channelId
            this[MeasurementTable.dateTime] = dto.toDbDateTime()
            this[MeasurementTable.value] = dto.value.toFloat()
            this[MeasurementTable.status] = dto.status != 0
            this[MeasurementTable.dataSource] = source.toDbValue()
        }

        deleted to measurements.size
    }

    private fun toDto(row: ResultRow): MeasurementDto {
        val channelId = row[MeasurementTable.channelId]

        return MeasurementDto(
            id = row[MeasurementTable.id],
            stationId = findStationIdForChannel(channelId),
            channelId = channelId,
            dateTime = row[MeasurementTable.dateTime].toDtoDateTime(),
            value = row[MeasurementTable.value].toDouble(),
            status = if (row[MeasurementTable.status]) 1 else 0,
            source = row[MeasurementTable.dataSource].toDataSourceDto()
        )
    }

    private fun findStationIdForChannel(channelId: Int): Long {
        return ChannelTable
            .selectAll()
            .where { ChannelTable.id eq channelId }
            .map { row -> row[ChannelTable.stationId] }
            .singleOrNull()
            ?: 0L
    }

    private fun MeasurementDto.toDbDateTime(): LocalDateTime {
        return ApiDateTime
            .toAppLocal(dateTime)
            .toKotlinLocalDateTime()
    }

    private fun LocalDateTime.toDtoDateTime(): OffsetDateTime {
        return ApiDateTime.fromAppLocal(toJavaLocalDateTime())
    }

    fun existsInRange(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): Boolean = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
            .where {
                (MeasurementTable.channelId eq channelId) and
                        (MeasurementTable.dateTime greaterEq from) and
                        (MeasurementTable.dateTime lessEq to)
            }
            .limit(1)
            .any()
    }
}