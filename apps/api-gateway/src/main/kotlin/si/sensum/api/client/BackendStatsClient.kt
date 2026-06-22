package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.stats.ChannelSummaryDto
import si.sensum.shared.models.stats.MeasurementRangeStatsDto
import si.sensum.shared.models.stats.OverviewStatsDto
import si.sensum.shared.models.stats.StationSummaryDto
import si.sensum.shared.models.stats.StationTimeRangeDto

internal class BackendStatsClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getOverview(): OverviewStatsDto {
        return backend.get("/api/v1/stats/overview")
    }

    suspend fun getMeasurementsInRange(
        from: String,
        to: String,
        stationId: Long? = null,
        channelId: Long? = null
    ): MeasurementRangeStatsDto {
        val url = buildString {
            append("/api/v1/stats/measurements/range?from=${enc(from)}&to=${enc(to)}")
            if (stationId != null) append("&stationId=$stationId")
            if (channelId != null) append("&channelId=$channelId")
        }
        return backend.get(url)
    }

    suspend fun getStations(): List<StationSummaryDto> {
        return backend.get("/api/v1/stats/stations")
    }

    suspend fun getChannelsForStation(stationId: Long): List<ChannelSummaryDto> {
        return backend.get("/api/v1/stats/stations/$stationId/channels")
    }

    suspend fun getStationTimeRange(stationId: Long): StationTimeRangeDto {
        return backend.get("/api/v1/stats/stations/$stationId/timerange")
    }

    private fun enc(value: String): String =
        java.net.URLEncoder.encode(value, Charsets.UTF_8)
}
