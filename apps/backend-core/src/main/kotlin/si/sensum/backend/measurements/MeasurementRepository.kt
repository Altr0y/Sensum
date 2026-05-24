package si.sensum.backend.measurements

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
import si.sensum.backend.channels.ChannelTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.shared.models.api.measurements.MeasurementDto
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

    fun existsInRange(
        from: LocalDateTime,
        to: LocalDateTime
    ): Boolean = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
            .where {
                (MeasurementTable.dateTime greaterEq from) and
                        (MeasurementTable.dateTime lessEq to)
            }
            .limit(1)
            .count() > 0
    }

    fun create(dto: MeasurementDto): MeasurementDto = DatabaseTransaction.run {
        val inserted = MeasurementTable.insert {
            it[channelId] = dto.channelId
            it[dateTime] = dto.toDbDateTime()
            it[value] = dto.value.toFloat()
            it[status] = dto.status != 0
        }

        dto.copy(id = inserted[MeasurementTable.id])
    }

    fun batchInsert(measurements: List<MeasurementEntity>) = DatabaseTransaction.run {
        MeasurementTable.batchInsert(measurements) { measurement ->
            this[MeasurementTable.channelId] = measurement.channelId
            this[MeasurementTable.dateTime] = measurement.dateTime
            this[MeasurementTable.value] = measurement.value
            this[MeasurementTable.status] = measurement.status
        }
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



    private fun findStationIdForChannel(channelId: Int): Long {
        return ChannelTable
            .selectAll()
            .where { ChannelTable.id eq channelId }
            .map { row -> row[ChannelTable.stationId] }
            .singleOrNull()
            ?: 0L
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

    private fun MeasurementDto.toDbDateTime(): LocalDateTime {
        return dateTime
            .toLocalDateTime()
            .toKotlinLocalDateTime()
    }

    private fun LocalDateTime.toDtoDateTime(): OffsetDateTime {
        return this
            .toJavaLocalDateTime()
            .atOffset(ZoneOffset.UTC)
    }
}