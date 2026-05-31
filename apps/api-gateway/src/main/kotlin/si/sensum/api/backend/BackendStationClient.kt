package si.sensum.api.backend

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.stations.StationDto

internal class BackendStationClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getStations(): List<StationDto> {
        return backend.get("/api/v1/stations")
    }

    suspend fun getStationById(stationId: Long): StationDto {
        return backend.get("/api/v1/stations/$stationId")
    }
}