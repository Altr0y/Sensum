package si.sensum.backend.repository

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.ChannelTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.MeasurementTable
import si.sensum.backend.database.StationTable
import si.sensum.shared.models.stats.ChannelSeriesDto
import si.sensum.shared.models.stats.CountByLabelDto
import si.sensum.shared.models.stats.RecentMeasurementDto
import si.sensum.shared.models.stats.TimeSeriesPointDto
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.math.round

class StatsRepository {

    fun stationCount(): Long = DatabaseTransaction.run {
        StationTable.selectAll().count()
    }

    fun channelCount(): Long = DatabaseTransaction.run {
        ChannelTable.selectAll().count()
    }

    fun measurementCount(): Long = DatabaseTransaction.run {
        MeasurementTable.selectAll().count()
    }

    fun measurementsBySource(): List<CountByLabelDto> = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
            .map { it[MeasurementTable.dataSource] }
            .groupingBy { it }
            .eachCount()
            .map { (source, count) -> CountByLabelDto(label = source, count = count.toLong()) }
            .sortedByDescending { it.count }
    }

    fun channelsByUnit(): List<CountByLabelDto> = DatabaseTransaction.run {
        ChannelTable
            .selectAll()
            .map { it[ChannelTable.unit].name }
            .groupingBy { it }
            .eachCount()
            .map { (unit, count) -> CountByLabelDto(label = unit, count = count.toLong()) }
            .sortedByDescending { it.count }
    }

    fun alarmCountSince(since: LocalDateTime): Long = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
            .where { (MeasurementTable.status eq false) and (MeasurementTable.dateTime greaterEq since) }
            .count()
    }

    fun alarmCountInRange(from: LocalDateTime, to: LocalDateTime): Long = DatabaseTransaction.run {
        MeasurementTable
            .selectAll()
            .where {
                (MeasurementTable.status eq false) and
                    (MeasurementTable.dateTime greaterEq from) and
                    (MeasurementTable.dateTime lessEq to)
            }
            .count()
    }

    fun aggregateInRange(from: LocalDateTime, to: LocalDateTime): Triple<Double?, Double?, Double?> = DatabaseTransaction.run {
        val values = MeasurementTable
            .selectAll()
            .where { (MeasurementTable.dateTime greaterEq from) and (MeasurementTable.dateTime lessEq to) }
            .map { it[MeasurementTable.value].toDouble() }

        if (values.isEmpty()) {
            Triple(null, null, null)
        } else {
            Triple(values.average().rounded(), values.min().rounded(), values.max().rounded())
        }
    }

    fun recentMeasurements(limit: Int): List<RecentMeasurementDto> = DatabaseTransaction.run {
        joinedQuery()
            .orderBy(MeasurementTable.dateTime to SortOrder.DESC)
            .limit(limit)
            .map(::toRecentDto)
    }

    fun measurementsInRange(
        from: LocalDateTime,
        to: LocalDateTime,
        limit: Int
    ): List<RecentMeasurementDto> = DatabaseTransaction.run {
        joinedQuery()
            .where { (MeasurementTable.dateTime greaterEq from) and (MeasurementTable.dateTime lessEq to) }
            .orderBy(MeasurementTable.dateTime to SortOrder.DESC)
            .limit(limit)
            .map(::toRecentDto)
    }

    fun seriesInRange(from: LocalDateTime, to: LocalDateTime): List<ChannelSeriesDto> = DatabaseTransaction.run {
        joinedQuery()
            .where { (MeasurementTable.dateTime greaterEq from) and (MeasurementTable.dateTime lessEq to) }
            .orderBy(MeasurementTable.dateTime to SortOrder.ASC)
            .map { row ->
                val label = "${row[StationTable.alias]} / ${row[ChannelTable.name]} (${row[ChannelTable.unit].name})"
                val point = TimeSeriesPointDto(
                    dateTime = row[MeasurementTable.dateTime].toDtoDateTime(),
                    value = row[MeasurementTable.value].toDouble().rounded()
                )
                label to point
            }
            .groupBy({ it.first }, { it.second })
            .map { (label, points) -> ChannelSeriesDto(label = label, points = points) }
    }

    private fun joinedQuery(): Query =
        MeasurementTable
            .join(ChannelTable, JoinType.INNER, onColumn = MeasurementTable.channelId, otherColumn = ChannelTable.id)
            .join(StationTable, JoinType.INNER, onColumn = ChannelTable.stationId, otherColumn = StationTable.id)
            .selectAll()

    private fun toRecentDto(row: ResultRow): RecentMeasurementDto = RecentMeasurementDto(
        dateTime = row[MeasurementTable.dateTime].toDtoDateTime(),
        station = row[StationTable.alias],
        serialNumber = row[StationTable.serialNumber],
        channel = row[ChannelTable.name],
        unit = row[ChannelTable.unit].name,
        value = row[MeasurementTable.value].toDouble().rounded(),
        status = row[MeasurementTable.status],
        source = row[MeasurementTable.dataSource]
    )

    private fun LocalDateTime.toDtoDateTime(): OffsetDateTime =
        toJavaLocalDateTime().atOffset(ZoneOffset.UTC)

    private fun Double.rounded(): Double = round(this * 1000) / 1000.0
}
