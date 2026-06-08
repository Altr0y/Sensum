package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.StationRepository
import si.sensum.shared.models.stations.CreateStationCommand
import si.sensum.shared.models.stations.StationDto

class StationService(
    private val stationRepository: StationRepository
) {
    fun getAllStations(): List<StationDto> {
        return stationRepository
            .findAll()
            .map { it.toDto() }
    }

    fun getStation(
        stationId: Long
    ): StationDto {
        return stationRepository
            .findById(stationId)
            ?.toDto()
            ?: error("Station not found")
    }

    fun getStationsByCustomer(
        customerId: Int
    ): List<StationDto> {
        return stationRepository
            .findByCustomerId(customerId)
            .map { it.toDto() }
    }

    fun createStation(command: CreateStationCommand): StationDto {
        require(command.name.isNotBlank()) { "Station name must not be blank" }
        require(command.customerId > 0) { "Customer id must be positive" }

        return stationRepository.create(
            customerId = command.customerId,
            name = command.name.trim(),
            latitude = command.latitude,
            longitude = command.longitude,
            description = command.description.trim(),
            serialNumber = command.serialNumber.trim(),
            source = command.source
        ).toDto()
    }

    fun getStationsByMunicipality(
        municipalityId: Int
    ): List<StationDto> {
        return stationRepository
            .findByMunicipalityId(municipalityId)
            .map { it.toDto() }
    }
}