package si.sensum.api.service

import si.sensum.api.client.BackendStatsClient
import si.sensum.shared.models.stats.MeasurementRangeStatsDto
import si.sensum.shared.models.stats.OverviewStatsDto

internal class StatsService(
    private val statsClient: BackendStatsClient
) {
    suspend fun getOverview(): OverviewStatsDto = statsClient.getOverview()

    suspend fun getMeasurementsInRange(from: String, to: String): MeasurementRangeStatsDto =
        statsClient.getMeasurementsInRange(from = from, to = to)
}
