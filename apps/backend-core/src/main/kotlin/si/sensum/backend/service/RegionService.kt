package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.RegionRepository
import si.sensum.shared.models.regions.RegionDto

class RegionService(
    private val regionRepository: RegionRepository
) {
    fun getAllRegions(): List<RegionDto> {
        return regionRepository
            .findAll()
            .map { it.toDto() }
    }

    fun getRegion(
        regionId: Int
    ): RegionDto {
        return regionRepository
            .findById(regionId)
            ?.toDto()
            ?: error("Region not found")
    }

    fun getRegionsByCountry(
        countryId: Int
    ): List<RegionDto> {
        return regionRepository
            .findByCountryId(countryId)
            .map { it.toDto() }
    }
}