package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.CountryRepository
import si.sensum.shared.models.countries.CountryDto

class CountryService(
    private val countryRepository: CountryRepository
) {
    fun getAllCountries(): List<CountryDto> {
        return countryRepository
            .findAll()
            .map { it.toDto() }
    }

    fun getCountry(
        countryId: Int
    ): CountryDto {
        return countryRepository
            .findById(countryId)
            ?.toDto()
            ?: error("Country not found")
    }
}