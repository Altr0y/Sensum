package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.MunicipalityRepository
import si.sensum.shared.models.municipalities.MunicipalityDto

class MunicipalityService(
    private val municipalityRepository: MunicipalityRepository
) {
    fun getAllMunicipalities(): List<MunicipalityDto> = ServiceLogger.call(
        service = "municipality",
        operation = "getAllMunicipalities"
    ) {
        municipalityRepository
            .findAll()
            .map { it.toDto() }
    }

    fun getMunicipality(
        municipalityId: Int
    ): MunicipalityDto = ServiceLogger.call(
        service = "municipality",
        operation = "getMunicipality",
        details = "municipalityId=$municipalityId"
    ) {
        municipalityRepository
            .findById(municipalityId)
            ?.toDto()
            ?: error("Municipality not found")
    }

    fun getMunicipalitiesByRegion(
        regionId: Int
    ): List<MunicipalityDto> = ServiceLogger.call(
        service = "municipality",
        operation = "getMunicipalitiesByRegion",
        details = "regionId=$regionId"
    ) {
        municipalityRepository
            .findByRegionId(regionId)
            .map { it.toDto() }
    }
}