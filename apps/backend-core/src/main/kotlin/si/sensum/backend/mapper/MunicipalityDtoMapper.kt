package si.sensum.backend.mapper

import si.sensum.backend.domain.location.MunicipalityEntity
import si.sensum.shared.models.municipalities.MunicipalityDto

fun MunicipalityEntity.toDto(): MunicipalityDto {
    return MunicipalityDto(
        id = id,
        countryId = countryId,
        regionId = regionId,
        name = name,
        geometry = geometry,
        source = source
    )
}