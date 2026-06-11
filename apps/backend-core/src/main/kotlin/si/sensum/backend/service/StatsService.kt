package si.sensum.backend.service

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import si.sensum.backend.repository.StatsRepository
import si.sensum.shared.models.stats.MeasurementRangeStatsDto
import si.sensum.shared.models.stats.OverviewStatsDto
import si.sensum.shared.models.stats.StationTimeRangeDto

private const val RECENT_MEASUREMENTS_LIMIT = 50
private const val RANGE_TABLE_LIMIT = 500
private const val ACTIVE_ALARM_WINDOW_HOURS = 1L

class StatsService(
    private val statsRepository: StatsRepository
) {
    fun overview(): OverviewStatsDto = ServiceLogger.call(
        service = "stats",
        operation = "overview"
    ) {
        val activeAlarmsSince = java.time.LocalDateTime.now()
            .minusHours(ACTIVE_ALARM_WINDOW_HOURS)
            .toKotlinLocalDateTime()

        OverviewStatsDto(
            stationCount = statsRepository.stationCount(),
            channelCount = statsRepository.channelCount(),
            measurementCount = statsRepository.measurementCount(),
            measurementsBySource = statsRepository.measurementsBySource(),
            channelsByUnit = statsRepository.channelsByUnit(),
            activeAlarms = statsRepository.alarmCountSince(activeAlarmsSince),
            recentMeasurements = statsRepository.recentMeasurements(RECENT_MEASUREMENTS_LIMIT)
        )
    }

    fun getStations() = statsRepository.getStations()

    fun stationTimeRange(stationId: Long) = statsRepository.stationTimeRange(stationId).let {
        StationTimeRangeDto(from = it.first, to = it.second)
    }

    fun measurementsInRange(
        from: LocalDateTime,
        to: LocalDateTime,
        stationId: Long? = null
    ): MeasurementRangeStatsDto = ServiceLogger.call(
        service = "stats",
        operation = "measurementsInRange",
        details = "from=$from to=$to stationId=$stationId"
    ) {
        val (average, minimum, maximum) = statsRepository.aggregateInRange(from, to, stationId)
        MeasurementRangeStatsDto(
            average = average,
            minimum = minimum,
            maximum = maximum,
            alarmCount = statsRepository.alarmCountInRange(from, to, stationId),
            series = statsRepository.seriesInRange(from, to, stationId),
            table = statsRepository.measurementsInRange(from, to, RANGE_TABLE_LIMIT, stationId)
        )
    }
}