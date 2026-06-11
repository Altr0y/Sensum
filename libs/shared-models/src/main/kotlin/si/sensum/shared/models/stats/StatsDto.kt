package si.sensum.shared.models.stats

import kotlinx.serialization.Serializable
import si.sensum.shared.models.serialization.OffsetDateTimeIsoSerializer
import java.time.OffsetDateTime

@Serializable
data class CountByLabelDto(
    val label: String,
    val count: Long
)

@Serializable
data class RecentMeasurementDto(
    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val dateTime: OffsetDateTime,
    val station: String,
    val serialNumber: String,
    val channel: String,
    val unit: String,
    val value: Double,
    val status: Boolean,
    val source: String
)

@Serializable
data class OverviewStatsDto(
    val stationCount: Long,
    val channelCount: Long,
    val measurementCount: Long,
    val measurementsBySource: List<CountByLabelDto>,
    val channelsByUnit: List<CountByLabelDto>,
    val activeAlarms: Long,
    val recentMeasurements: List<RecentMeasurementDto>
)

@Serializable
data class TimeSeriesPointDto(
    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val dateTime: OffsetDateTime,
    val value: Double
)

@Serializable
data class ChannelSeriesDto(
    val label: String,
    val points: List<TimeSeriesPointDto>
)

@Serializable
data class MeasurementRangeStatsDto(
    val average: Double?,
    val minimum: Double?,
    val maximum: Double?,
    val alarmCount: Long,
    val series: List<ChannelSeriesDto>,
    val table: List<RecentMeasurementDto>
)

@Serializable
data class StationSummaryDto(
    val id: Long,
    val name: String
)


@Serializable
data class StationTimeRangeDto(
    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val from: OffsetDateTime?,
    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val to: OffsetDateTime?
)