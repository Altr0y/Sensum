package si.sensum.backend.mapper

import si.sensum.backend.domain.location.CountryEntity
import si.sensum.shared.models.countries.CountryDto

fun CountryEntity.toDto(): CountryDto {
    return CountryDto(
        id = id,
        code = code,
        name = name,
        originName = originName,
        geometry = geometry,
        source = source
    )
}