package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.stats.MeasurementRangeStatsDto
import si.sensum.shared.models.stats.OverviewStatsDto

internal class BackendStatsClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getOverview(): OverviewStatsDto {
        return backend.get("/api/v1/stats/overview")
    }

    suspend fun getMeasurementsInRange(from: String, to: String): MeasurementRangeStatsDto {
        return backend.get("/api/v1/stats/measurements/range?from=${enc(from)}&to=${enc(to)}")
    }

    private fun enc(value: String): String =
        java.net.URLEncoder.encode(value, Charsets.UTF_8)
}
