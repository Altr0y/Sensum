package si.sensum.backend.measurements

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import si.sensum.backend.channels.ChannelTable
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.shared.models.api.measurements.MeasurementDto
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MeasurementRepository {

    fun findAll(): List<MeasurementDto> = DatabaseTransaction.run {
    fun findByChannelAndRange(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Measurement> = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
            .orderBy(
                MeasurementTable.channelId to SortOrder.ASC,
                MeasurementTable.dateTime to SortOrder.ASC
            )
            .map(::toDto)
    }

    fun create(dto: MeasurementDto): MeasurementDto = DatabaseTransaction.run {
        val inserted = MeasurementTable.insert {
            it[channelId] = dto.channelId
            it[dateTime] = dto.toDbDateTime()
            it[value] = dto.value.toFloat()
            it[status] = dto.status != 0
        }

        dto.copy(id = inserted[MeasurementTable.id])
            .where {
                (MeasurementTable.channelId eq channelId) and
                        (MeasurementTable.dateTime greaterEq from) and
                        (MeasurementTable.dateTime lessEq to)
            }
            .map(::toMeasurement)
    }

    fun existsInRange(from: LocalDateTime, to: LocalDateTime): Boolean = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
            .where {
                (MeasurementTable.dateTime greaterEq from) and
                        (MeasurementTable.dateTime lessEq to)
            }
            .limit(1)
            .count() > 0
    fun createAll(measurements: List<MeasurementDto>): List<MeasurementDto> {
        return measurements.map { create(it) }
    }

    fun update(id: Long, dto: MeasurementDto): MeasurementDto? = DatabaseTransaction.run {
        val updatedCount = MeasurementTable.update({ MeasurementTable.id eq id }) {
            it[channelId] = dto.channelId
            it[dateTime] = dto.toDbDateTime()
            it[value] = dto.value.toFloat()
            it[status] = dto.status != 0
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

    fun batchInsert(measurements: List<Measurement>): Unit = DatabaseTransaction.run {
        MeasurementTable.batchInsert(measurements) { m ->
            this[MeasurementTable.channelId] = m.channelId
            this[MeasurementTable.dateTime] = m.dateTime
            this[MeasurementTable.value] = m.value
            this[MeasurementTable.status] = m.status
        }
    }

    private fun toMeasurement(row: ResultRow): Measurement {
        return Measurement(
            id = row[MeasurementTable.id],
            channelId = row[MeasurementTable.channelId],
            dateTime = row[MeasurementTable.dateTime],
            value = row[MeasurementTable.value],
            status = row[MeasurementTable.status]
        )
    }

    fun deleteByRange(from: LocalDateTime, to: LocalDateTime): Unit = DatabaseTransaction.run {
    fun delete(id: Long): Boolean = DatabaseTransaction.run {
        MeasurementTable.deleteWhere {
            (MeasurementTable.dateTime greaterEq from) and
                    (MeasurementTable.dateTime lessEq to)
        }
            MeasurementTable.id eq id
        } > 0
    }

    fun deleteAll(): Int = DatabaseTransaction.run {
        MeasurementTable.deleteAll()
    }

    fun replaceAllFromDtos(measurements: List<MeasurementDto>): Pair<Int, Int> =
        DatabaseTransaction.run {
            val deleted = MeasurementTable.deleteAll()

            measurements.forEach { dto ->
                MeasurementTable.insert {
                    it[channelId] = dto.channelId
                    it[dateTime] = dto.toDbDateTime()
                    it[value] = dto.value.toFloat()
                    it[status] = dto.status != 0
                }
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
            status = if (row[MeasurementTable.status]) 1 else 0
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

    private fun MeasurementDto.toDbDateTime(): kotlinx.datetime.LocalDateTime {
        return dateTime
            .toLocalDateTime()
            .toKotlinLocalDateTime()
    }

    private fun kotlinx.datetime.LocalDateTime.toDtoDateTime(): OffsetDateTime {
        return this
            .toJavaLocalDateTime()
            .atOffset(ZoneOffset.UTC)
    }
}