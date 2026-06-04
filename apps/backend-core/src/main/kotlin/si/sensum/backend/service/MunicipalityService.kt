package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.MunicipalityRepository
import si.sensum.shared.models.municipalities.MunicipalityDto

class MunicipalityService(
    private val municipalityRepository: MunicipalityRepository
) {
    fun getAllMunicipalities(): List<MunicipalityDto> {
        return municipalityRepository
            .findAll()
            .map { it.toDto() }
    }

    fun getMunicipality(
        municipalityId: Int
    ): MunicipalityDto {
        return municipalityRepository
            .findById(municipalityId)
            ?.toDto()
            ?: error("Municipality not found")
    }

    fun getMunicipalitiesByRegion(
        regionId: Int
    ): List<MunicipalityDto> {
        return municipalityRepository
            .findByRegionId(regionId)
            .map { it.toDto() }
    }
}