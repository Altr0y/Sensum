package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.ChannelTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.MeasurementTable
import si.sensum.backend.database.StationTable
import si.sensum.backend.domain.records.FilterOperator
import si.sensum.backend.domain.records.RecordFilter
import si.sensum.backend.domain.records.RecordsQuery
import si.sensum.backend.domain.records.SortDirection
import si.sensum.backend.mapper.toDataSourceDto
import si.sensum.shared.models.records.ChannelRecordDto
import si.sensum.shared.models.records.MeasurementRecordDto
import si.sensum.shared.models.records.RecordsPageDto
import si.sensum.shared.models.records.StationRecordDto

private const val MAX_PAGE_SIZE = 100

class RecordsRepository {

    fun findStations(query: RecordsQuery): RecordsPageDto<StationRecordDto> =
        DatabaseTransaction.run {
            StationTable
                .selectAll()
                .map(::toStationRecord)
                .filterRecords(query, ::stationValueOf)
                .sortRecords(query, ::stationValueOf)
                .toPage(query)
        }

    fun findChannels(query: RecordsQuery): RecordsPageDto<ChannelRecordDto> =
        DatabaseTransaction.run {
            ChannelTable
                .selectAll()
                .map(::toChannelRecord)
                .filterRecords(query, ::channelValueOf)
                .sortRecords(query, ::channelValueOf)
                .toPage(query)
        }

    fun findMeasurements(query: RecordsQuery): RecordsPageDto<MeasurementRecordDto> =
        DatabaseTransaction.run {
            val stationsById = StationTable
                .selectAll()
                .associate { row ->
                    row[StationTable.id] to row[StationTable.alias]
                }

            val channelsById = ChannelTable
                .selectAll()
                .associate { row ->
                    row[ChannelTable.id] to ChannelLookup(
                        stationId = row[ChannelTable.stationId],
                        channelName = row[ChannelTable.name]
                    )
                }

            MeasurementTable
                .selectAll()
                .map { row ->
                    toMeasurementRecord(
                        row = row,
                        channelsById = channelsById,
                        stationsById = stationsById
                    )
                }
                .filterRecords(query, ::measurementValueOf)
                .sortRecords(query, ::measurementValueOf)
                .toPage(query)
        }

    private fun toStationRecord(row: ResultRow): StationRecordDto {
        return StationRecordDto(
            stationId = row[StationTable.id],
            name = row[StationTable.alias],
            serialNumber = row[StationTable.serialNumber],
            description = row[StationTable.locationDescription],
            latitude = row[StationTable.latitude],
            longitude = row[StationTable.longitude],
            countryId = row[StationTable.countryId],
            regionId = row[StationTable.regionId],
            municipalityId = row[StationTable.municipalityId],
            source = row[StationTable.dataSource].toDataSourceDto()
        )
    }

    private fun toChannelRecord(row: ResultRow): ChannelRecordDto {
        return ChannelRecordDto(
            channelId = row[ChannelTable.id],
            stationId = row[ChannelTable.stationId],
            name = row[ChannelTable.name],
            unit = row[ChannelTable.unit].name,
            description = row[ChannelTable.description],
            source = row[ChannelTable.dataSource].toDataSourceDto()
        )
    }

    private fun toMeasurementRecord(
        row: ResultRow,
        channelsById: Map<Int, ChannelLookup>,
        stationsById: Map<Long, String>
    ): MeasurementRecordDto {
        val channelId = row[MeasurementTable.channelId]
        val channel = channelsById[channelId]
        val stationId = channel?.stationId ?: 0L

        return MeasurementRecordDto(
            id = row[MeasurementTable.id],
            stationId = stationId,
            stationName = stationsById[stationId],
            channelId = channelId,
            channelName = channel?.channelName,
            dateTime = row[MeasurementTable.dateTime].toString(),
            value = row[MeasurementTable.value].toDouble(),
            status = if (row[MeasurementTable.status]) 1 else 0,
            source = row[MeasurementTable.dataSource].toDataSourceDto()
        )
    }

    private fun stationValueOf(
        item: StationRecordDto,
        field: String
    ): String? {
        return when (field) {
            "id", "stationId" -> item.stationId.toString()
            "name" -> item.name
            "serialNumber" -> item.serialNumber
            "description" -> item.description
            "latitude" -> item.latitude?.toString()
            "longitude" -> item.longitude?.toString()
            "countryId" -> item.countryId?.toString()
            "regionId" -> item.regionId?.toString()
            "municipalityId" -> item.municipalityId?.toString()
            "source" -> item.source.name
            else -> null
        }
    }

    private fun channelValueOf(
        item: ChannelRecordDto,
        field: String
    ): String? {
        return when (field) {
            "id", "channelId" -> item.channelId.toString()
            "stationId" -> item.stationId.toString()
            "name" -> item.name
            "unit" -> item.unit
            "description" -> item.description
            "source" -> item.source.name
            else -> null
        }
    }

    private fun measurementValueOf(
        item: MeasurementRecordDto,
        field: String
    ): String? {
        return when (field) {
            "id" -> item.id?.toString()
            "stationId" -> item.stationId.toString()
            "stationName" -> item.stationName
            "channelId" -> item.channelId.toString()
            "channelName" -> item.channelName
            "dateTime" -> item.dateTime
            "value" -> item.value.toString()
            "status" -> item.status.toString()
            "source" -> item.source.name
            else -> null
        }
    }

    private fun <T> List<T>.filterRecords(
        query: RecordsQuery,
        valueOf: (T, String) -> String?
    ): List<T> {
        return filter { item ->
            query.filters.all { filter ->
                val rawValue = valueOf(item, filter.field) ?: return@all false
                rawValue.matchesFilter(filter)
            }
        }
    }

    private fun String.matchesFilter(
        filter: RecordFilter
    ): Boolean {
        val rawNumber = toDoubleOrNull()
        val expectedNumber = filter.value.toDoubleOrNull()

        return when (filter.operator) {
            FilterOperator.EQ -> equals(filter.value, ignoreCase = true)

            FilterOperator.CONTAINS -> contains(
                other = filter.value,
                ignoreCase = true
            )

            FilterOperator.GT ->
                rawNumber != null && expectedNumber != null && rawNumber > expectedNumber

            FilterOperator.LT ->
                rawNumber != null && expectedNumber != null && rawNumber < expectedNumber

            FilterOperator.GTE ->
                rawNumber != null && expectedNumber != null && rawNumber >= expectedNumber

            FilterOperator.LTE ->
                rawNumber != null && expectedNumber != null && rawNumber <= expectedNumber
        }
    }

    private fun <T> List<T>.sortRecords(
        query: RecordsQuery,
        valueOf: (T, String) -> String?
    ): List<T> {
        return sortedWith { left, right ->
            val leftValue = valueOf(left, query.sortBy).orEmpty()
            val rightValue = valueOf(right, query.sortBy).orEmpty()

            val result = compareSmart(leftValue, rightValue)

            if (query.sortDirection == SortDirection.ASC) {
                result
            } else {
                -result
            }
        }
    }

    private fun compareSmart(
        left: String,
        right: String
    ): Int {
        val leftNumber = left.toDoubleOrNull()
        val rightNumber = right.toDoubleOrNull()

        return if (leftNumber != null && rightNumber != null) {
            leftNumber.compareTo(rightNumber)
        } else {
            left.compareTo(right, ignoreCase = true)
        }
    }

    private fun <T> List<T>.toPage(
        query: RecordsQuery
    ): RecordsPageDto<T> {
        val safePageSize = query.pageSize.coerceIn(1, MAX_PAGE_SIZE)
        val totalPages = ((size + safePageSize - 1) / safePageSize).coerceAtLeast(1)
        val safePage = query.page.coerceIn(0, totalPages - 1)

        return RecordsPageDto(
            items = drop(safePage * safePageSize).take(safePageSize),
            page = safePage,
            pageSize = safePageSize,
            totalItems = size,
            totalPages = totalPages
        )
    }

    private data class ChannelLookup(
        val stationId: Long,
        val channelName: String
    )
}