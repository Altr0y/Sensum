package si.sensum.backend.measurements

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import si.sensum.backend.database.DatabaseTransaction

class MeasurementRepository {

    fun findByChannelAndRange(
        channelId: Int,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Measurement> = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
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
        MeasurementTable.deleteWhere {
            (MeasurementTable.dateTime greaterEq from) and
                    (MeasurementTable.dateTime lessEq to)
        }
    }
}