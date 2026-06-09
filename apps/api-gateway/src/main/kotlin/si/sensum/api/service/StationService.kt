package si.sensum.api.service

import kotlinx.serialization.Serializable
import si.sensum.api.client.BackendStationClient
import si.sensum.shared.models.stations.CreateStationCommand
import si.sensum.shared.models.stations.StationDto
import si.sensum.api.domain.ApiPrincipal

internal class StationService(
    private val stations: BackendStationClient
) {
    suspend fun getStations(): List<StationDto> {
        return stations.getStations()
    }

    suspend fun getStationsGeoJson(): StationGeoJsonFeatureCollectionDto {
        val features = stations
            .getStations()
            .filter { station ->
                station.latitude != null && station.longitude != null
            }
            .map { station ->
                station.toGeoJsonFeature()
            }

        return StationGeoJsonFeatureCollectionDto(
            features = features
        )
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

    suspend fun getVisibleStations(
        principal: ApiPrincipal
    ): List<StationDto> {
        return if (principal.isAdmin()) {
            stations.getStationsByCustomer(principal.customerId)
        } else {
            stations.getStationsByUser(
                customerId = principal.customerId,
                userId = principal.userId
            )
        }
    }

    suspend fun getVisibleStationById(
        principal: ApiPrincipal,
        stationId: Long
    ): StationDto {
        require(stationId > 0) {
            "Station id must be positive"
        }

        return if (principal.isAdmin()) {
            stations.getStationByCustomer(
                customerId = principal.customerId,
                stationId = stationId
            )
        } else {
            stations.getStationByUser(
                customerId = principal.customerId,
                userId = principal.userId,
                stationId = stationId
            )
        }
    }

    suspend fun getVisibleStationsGeoJson(
        principal: ApiPrincipal
    ): StationGeoJsonFeatureCollectionDto {
        val features = getVisibleStations(principal)
            .filter { station ->
                station.latitude != null && station.longitude != null
            }
            .map { station ->
                station.toGeoJsonFeature()
            }

        return StationGeoJsonFeatureCollectionDto(
            features = features
        )
    }

    suspend fun createStation(command: CreateStationCommand): StationDto {
        return stations.createStation(command)
    }

    private fun StationDto.toGeoJsonFeature(): StationGeoJsonFeatureDto {
        return StationGeoJsonFeatureDto(
            properties = StationGeoJsonPropertiesDto(
                id = stationId,
                stationId = stationId,
                name = name ?: "Station $stationId",
                type = "station",
                layer = "stations",
                source = source.name,
                modbusAddress = modbusAddress,
                serialNumber = serialNumber,
                stationType = stationType,
                description = description
            ),
            geometry = StationGeoJsonPointGeometryDto(
                coordinates = listOf(
                    longitude ?: 0.0,
                    latitude ?: 0.0
                )
            )
        )
    }

    private fun ApiPrincipal.isAdmin(): Boolean {
        return role == "ADMIN"
    }
}

@Serializable
internal data class StationGeoJsonFeatureCollectionDto(
    val type: String = "FeatureCollection",
    val features: List<StationGeoJsonFeatureDto>
)

@Serializable
internal data class StationGeoJsonFeatureDto(
    val type: String = "Feature",
    val properties: StationGeoJsonPropertiesDto,
    val geometry: StationGeoJsonPointGeometryDto
)

@Serializable
internal data class StationGeoJsonPropertiesDto(
    val id: Long,
    val stationId: Long,
    val name: String,
    val type: String,
    val layer: String,
    val source: String? = null,
    val modbusAddress: Int? = null,
    val serialNumber: String? = null,
    val stationType: String? = null,
    val description: String? = null
)

@Serializable
internal data class StationGeoJsonPointGeometryDto(
    val type: String = "Point",
    val coordinates: List<Double>
)