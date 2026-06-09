package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.stations.CreateStationCommand
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

    suspend fun getStationsByCustomer(customerId: Int): List<StationDto> {
        return backend.get("/api/v1/customers/$customerId/stations")
    }

    suspend fun getStationsByUser(
        customerId: Int,
        userId: Int
    ): List<StationDto> {
        return backend.get("/api/v1/customers/$customerId/users/$userId/stations")
    }

    suspend fun getStationByCustomer(
        customerId: Int,
        stationId: Long
    ): StationDto {
        return backend.get("/api/v1/customers/$customerId/stations/$stationId")
    }

    suspend fun getStationByUser(
        customerId: Int,
        userId: Int,
        stationId: Long
    ): StationDto {
        return backend.get("/api/v1/customers/$customerId/users/$userId/stations/$stationId")
    }

    suspend fun createStation(command: CreateStationCommand): StationDto {
        return backend.post("/api/v1/stations", command)
    }
}