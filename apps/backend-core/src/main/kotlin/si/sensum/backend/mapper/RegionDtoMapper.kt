package si.sensum.backend.mapper

import si.sensum.backend.domain.location.RegionEntity
import si.sensum.shared.models.regions.RegionDto

fun RegionEntity.toDto(): RegionDto {
    return RegionDto(
        id = id,
        countryId = countryId,
        name = name,
        geometry = geometry,
        source = source
    )
}