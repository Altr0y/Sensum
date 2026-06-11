package si.sensum.api.service

import si.sensum.api.client.BackendStatsClient
import si.sensum.shared.models.stats.MeasurementRangeStatsDto
import si.sensum.shared.models.stats.OverviewStatsDto

internal class StatsService(
    private val statsClient: BackendStatsClient
) {
    suspend fun getOverview(): OverviewStatsDto = statsClient.getOverview()

    suspend fun getStations() = statsClient.getStations()

    suspend fun getStationTimeRange(stationId: Long) = statsClient.getStationTimeRange(stationId)

    suspend fun getMeasurementsInRange(
        from: String,
        to: String,
        stationId: Long? = null
    ): MeasurementRangeStatsDto =
        statsClient.getMeasurementsInRange(from = from, to = to, stationId = stationId)
}
