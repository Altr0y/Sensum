package si.sensum.backend.repository

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import si.sensum.backend.database.ChannelTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.MeasurementTable
import si.sensum.backend.database.StationTable
import si.sensum.shared.models.stats.*
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

    fun getStations(): List<StationSummaryDto> = DatabaseTransaction.run {
        StationTable.selectAll().map {
            StationSummaryDto(
                id = it[StationTable.id],
                name = it[StationTable.alias]
            )
        }
    }

    fun getChannelsForStation(stationId: Long): List<ChannelSummaryDto> = DatabaseTransaction.run {
        ChannelTable
            .selectAll()
            .where { ChannelTable.stationId eq stationId }
            .map {
                ChannelSummaryDto(
                    id = it[ChannelTable.id].toLong(),
                    name = it[ChannelTable.name]
                )
            }
    }

    fun stationTimeRange(stationId: Long): Pair<OffsetDateTime?, OffsetDateTime?> = DatabaseTransaction.run {
        val rows = MeasurementTable
            .join(ChannelTable, JoinType.INNER, onColumn = MeasurementTable.channelId, otherColumn = ChannelTable.id)
            .selectAll()
            .where { ChannelTable.stationId eq stationId }
            .orderBy(MeasurementTable.dateTime to SortOrder.ASC)
            .toList()

        if (rows.isEmpty()) return@run Pair(null, null)

        Pair(
            rows.first()[MeasurementTable.dateTime].toDtoDateTime(),
            rows.last()[MeasurementTable.dateTime].toDtoDateTime()
        )
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

    fun activeAlarmCount(): Long = DatabaseTransaction.run {
        val sql = """
        SELECT COUNT(*) FROM (
            SELECT DISTINCT ON (channel_id) channel_id, status
            FROM measurements
            ORDER BY channel_id, date_time DESC
        ) latest
        WHERE latest.status = false
    """.trimIndent()

        var count = 0L
        TransactionManager.current().exec(sql) { rs: java.sql.ResultSet ->
            if (rs.next()) {
                count = rs.getLong(1)
            }
        }
        count
    }

    fun getRecentStations(limit: Int = 5): List<RecentStationDto> = DatabaseTransaction.run {
        StationTable
            .selectAll()
            .orderBy(StationTable.id, SortOrder.DESC)
            .limit(limit)
            .map {
                RecentStationDto(
                    id = it[StationTable.id],
                    name = it[StationTable.alias],
                    source = it[StationTable.dataSource],
                    createdAt = it[StationTable.createdAt]?.toDtoDateTime()
                )
            }
    }

    fun alarmCountInRange(
        from: LocalDateTime,
        to: LocalDateTime,
        stationId: Long? = null,
        channelId: Long? = null
    ): Long = DatabaseTransaction.run {
        var query = joinedQuery()
            .where {
                (MeasurementTable.status eq false) and
                        (MeasurementTable.dateTime greaterEq from) and
                        (MeasurementTable.dateTime lessEq to)
            }
        if (stationId != null) {
            query = query.andWhere { ChannelTable.stationId eq stationId }
        }
        if (channelId != null) {
            query = query.andWhere { ChannelTable.id eq channelId.toInt() }
        }
        query.count()
    }

    fun aggregateInRange(
        from: LocalDateTime,
        to: LocalDateTime,
        stationId: Long? = null,
        channelId: Long? = null
    ): Triple<Double?, Double?, Double?> = DatabaseTransaction.run {

        var query = joinedQuery()
            .where { (MeasurementTable.dateTime greaterEq from) and (MeasurementTable.dateTime lessEq to) }
        if (stationId != null) {
            query = query.andWhere { ChannelTable.stationId eq stationId }
        }
        if (channelId != null) {
            query = query.andWhere { ChannelTable.id eq channelId.toInt() }
        }
        val values = query.map { it[MeasurementTable.value].toDouble() }
        if (values.isEmpty()) Triple(null, null, null)
        else Triple(values.average().rounded(), values.min().rounded(), values.max().rounded())
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
        limit: Int,
        stationId: Long? = null,
        channelId: Long? = null
    ): List<RecentMeasurementDto> = DatabaseTransaction.run {
        var query = joinedQuery()
            .where { (MeasurementTable.dateTime greaterEq from) and (MeasurementTable.dateTime lessEq to) }
        if (stationId != null) {
            query = query.andWhere { ChannelTable.stationId eq stationId }
        }
        if (channelId != null) {
            query = query.andWhere { ChannelTable.id eq channelId.toInt() }
        }
        query.orderBy(MeasurementTable.dateTime to SortOrder.DESC)
            .limit(limit)
            .map(::toRecentDto)
    }

    fun seriesInRange(
        from: LocalDateTime,
        to: LocalDateTime,
        stationId: Long? = null,
        channelId: Long? = null
    ): List<ChannelSeriesDto> = DatabaseTransaction.run {
        var query = joinedQuery()
            .where { (MeasurementTable.dateTime greaterEq from) and (MeasurementTable.dateTime lessEq to) }
        if (stationId != null) {
            query = query.andWhere { ChannelTable.stationId eq stationId }
        }
        if (channelId != null) {
            query = query.andWhere { ChannelTable.id eq channelId.toInt() }
        }
        query.orderBy(MeasurementTable.dateTime to SortOrder.ASC)
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
