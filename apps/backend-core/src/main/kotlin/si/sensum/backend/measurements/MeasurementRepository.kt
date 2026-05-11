package si.sensum.backend.measurements

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
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

    fun create(dto: MeasurementDto): MeasurementDto = DatabaseTransaction.run {
        val inserted = MeasurementTable.insert {
            it[stationId] = dto.stationId
            it[channelId] = dto.channelId
            it[dateTime] = dto.toDbDateTime()
            it[value] = dto.value
            it[status] = dto.status
        }

        dto.copy(id = inserted[MeasurementTable.id])
    }

    fun createAll(measurements: List<MeasurementDto>): List<MeasurementDto> {
        return measurements.map { create(it) }
    }

    fun update(id: Long, dto: MeasurementDto): MeasurementDto? = DatabaseTransaction.run {
        val updatedCount = MeasurementTable.update({ MeasurementTable.id eq id }) {
            it[stationId] = dto.stationId
            it[channelId] = dto.channelId
            it[dateTime] = dto.toDbDateTime()
            it[value] = dto.value
            it[status] = dto.status
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

    fun replaceAllFromDtos(measurements: List<MeasurementDto>): Pair<Int, Int> =
        DatabaseTransaction.run {
            val deleted = MeasurementTable.deleteAll()

            measurements.forEach { dto ->
                MeasurementTable.insert {
                    it[stationId] = dto.stationId
                    it[channelId] = dto.channelId
                    it[dateTime] = dto.toDbDateTime()
                    it[value] = dto.value
                    it[status] = dto.status
                }
            }

            deleted to measurements.size
        }

    private fun toDto(row: ResultRow): MeasurementDto {
        return MeasurementDto(
            id = row[MeasurementTable.id],
            stationId = row[MeasurementTable.stationId],
            channelId = row[MeasurementTable.channelId],
            dateTime = row[MeasurementTable.dateTime].toDtoDateTime(),
            value = row[MeasurementTable.value],
            status = row[MeasurementTable.status]
        )
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