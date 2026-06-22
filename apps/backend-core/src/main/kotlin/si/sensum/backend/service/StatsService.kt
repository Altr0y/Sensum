package si.sensum.backend.service

import kotlinx.datetime.LocalDateTime
import si.sensum.backend.repository.StatsRepository
import si.sensum.shared.models.stats.MeasurementRangeStatsDto
import si.sensum.shared.models.stats.OverviewStatsDto
import si.sensum.shared.models.stats.StationTimeRangeDto

private const val RECENT_MEASUREMENTS_LIMIT = 50
private const val RANGE_TABLE_LIMIT = 500

class StatsService(
    private val statsRepository: StatsRepository
) {
    fun overview(): OverviewStatsDto = ServiceLogger.call(
        service = "stats",
        operation = "overview"
    ) {
        OverviewStatsDto(
            stationCount = statsRepository.stationCount(),
            channelCount = statsRepository.channelCount(),
            measurementCount = statsRepository.measurementCount(),
            measurementsBySource = statsRepository.measurementsBySource(),
            channelsByUnit = statsRepository.channelsByUnit(),
            activeAlarms = statsRepository.activeAlarmCount(),
            recentMeasurements = statsRepository.recentMeasurements(RECENT_MEASUREMENTS_LIMIT),
            recentStations = statsRepository.getRecentStations()
        )
    }

    fun getStations() = statsRepository.getStations()

    fun getChannelsForStation(stationId: Long) = statsRepository.getChannelsForStation(stationId)

    fun stationTimeRange(stationId: Long) = statsRepository.stationTimeRange(stationId).let {
        StationTimeRangeDto(from = it.first, to = it.second)
    }

    fun measurementsInRange(
        from: LocalDateTime,
        to: LocalDateTime,
        stationId: Long? = null,
        channelId: Long? = null
    ): MeasurementRangeStatsDto = ServiceLogger.call(
        service = "stats",
        operation = "measurementsInRange",
        details = "from=$from to=$to stationId=$stationId channelId=$channelId"
    ) {
        val (average, minimum, maximum) = statsRepository.aggregateInRange(from, to, stationId, channelId)
        MeasurementRangeStatsDto(
            average = average,
            minimum = minimum,
            maximum = maximum,
            alarmCount = statsRepository.alarmCountInRange(from, to, stationId, channelId),
            series = statsRepository.seriesInRange(from, to, stationId, channelId),
            table = statsRepository.measurementsInRange(from, to, RANGE_TABLE_LIMIT, stationId, channelId)
        )
    }
}