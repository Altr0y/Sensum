package si.sensum.api.service

import si.sensum.api.client.BackendStationClient
import si.sensum.shared.models.stations.StationDto

internal class StationService(
    private val stations: BackendStationClient
) {
    suspend fun getStations(): List<StationDto> {
        return stations.getStations()
    }

    suspend fun getStationById(stationId: Long): StationDto {
        require(stationId > 0) {
            "Station id must be positive"
        }

        return stations.getStationById(stationId)
    }

    suspend fun getStationsByCustomer(customerId: Int): List<StationDto> {
        require(customerId > 0) {
            "Customer id must be positive"
        }

        return stations.getStationsByCustomer(customerId)
    }
}