package si.sensum.backend.mapper

import si.sensum.backend.domain.station.StationEntity
import si.sensum.shared.models.stations.StationDto

fun StationEntity.toDto(): StationDto {
    return StationDto(
        stationId = id,
        name = alias,
        serialNumber = serialNumber,
        description = locationDescription,
        latitude = latitude,
        longitude = longitude,
        source = source
    )
}