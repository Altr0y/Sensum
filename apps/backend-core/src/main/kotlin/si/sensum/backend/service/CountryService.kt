package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.CountryRepository
import si.sensum.shared.models.countries.CountryDto

class CountryService(
    private val countryRepository: CountryRepository
) {
    fun getAllCountries(): List<CountryDto> = ServiceLogger.call(
        service = "country",
        operation = "getAllCountries"
    ) {
        countryRepository
            .findAll()
            .map { it.toDto() }
    }

    fun getCountry(
        countryId: Int
    ): CountryDto = ServiceLogger.call(
        service = "country",
        operation = "getCountry",
        details = "countryId=$countryId"
    ) {
        countryRepository
            .findById(countryId)
            ?.toDto()
            ?: error("Country not found")
    }
}