package si.sensum.backend.service

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import si.sensum.backend.repository.StatsRepository
import si.sensum.shared.models.stats.MeasurementRangeStatsDto
import si.sensum.shared.models.stats.OverviewStatsDto

private const val RECENT_MEASUREMENTS_LIMIT = 50
private const val RANGE_TABLE_LIMIT = 500
private const val ACTIVE_ALARM_WINDOW_HOURS = 1L

class StatsService(
    private val statsRepository: StatsRepository
) {
    fun overview(): OverviewStatsDto {
        val activeAlarmsSince = java.time.LocalDateTime.now()
            .minusHours(ACTIVE_ALARM_WINDOW_HOURS)
            .toKotlinLocalDateTime()

        return OverviewStatsDto(
            stationCount = statsRepository.stationCount(),
            channelCount = statsRepository.channelCount(),
            measurementCount = statsRepository.measurementCount(),
            measurementsBySource = statsRepository.measurementsBySource(),
            channelsByUnit = statsRepository.channelsByUnit(),
            activeAlarms = statsRepository.alarmCountSince(activeAlarmsSince),
            recentMeasurements = statsRepository.recentMeasurements(RECENT_MEASUREMENTS_LIMIT)
        )
    }

    fun measurementsInRange(from: LocalDateTime, to: LocalDateTime): MeasurementRangeStatsDto {
        val (average, minimum, maximum) = statsRepository.aggregateInRange(from, to)

        return MeasurementRangeStatsDto(
            average = average,
            minimum = minimum,
            maximum = maximum,
            alarmCount = statsRepository.alarmCountInRange(from, to),
            series = statsRepository.seriesInRange(from, to),
            table = statsRepository.measurementsInRange(from, to, RANGE_TABLE_LIMIT)
        )
    }
}
