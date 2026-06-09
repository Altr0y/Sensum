package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.StationRepository
import si.sensum.shared.models.stations.CreateStationCommand
import si.sensum.shared.models.stations.StationDto

class StationService(
    private val stationRepository: StationRepository
) {
    fun getAllStations(): List<StationDto> = ServiceLogger.call(
        service = "station",
        operation = "getAllStations"
    ) {
        stationRepository
            .findAll()
            .map { it.toDto() }
    }

    fun getStation(
        stationId: Long
    ): StationDto = ServiceLogger.call(
        service = "station",
        operation = "getStation",
        details = "stationId=$stationId"
    ) {
        stationRepository
            .findById(stationId)
            ?.toDto()
            ?: error("Station not found")
    }

    fun getStationsByCustomer(
        customerId: Int
    ): List<StationDto> = ServiceLogger.call(
        service = "station",
        operation = "getStationsByCustomer",
        details = "customerId=$customerId"
    ) {
        stationRepository
            .findByCustomerId(customerId)
            .map { it.toDto() }
    }

    fun getStationByCustomer(
        customerId: Int,
        stationId: Long
    ): StationDto = ServiceLogger.call(
        service = "station",
        operation = "getStationByCustomer",
        details = "customerId=$customerId stationId=$stationId"
    ) {
        stationRepository
            .findByCustomerAndId(
                customerId = customerId,
                stationId = stationId
            )
            ?.toDto()
            ?: error("Station not found")
    }

    fun getStationsByUser(
        customerId: Int,
        userId: Int
    ): List<StationDto> = ServiceLogger.call(
        service = "station",
        operation = "getStationsByUser",
        details = "customerId=$customerId userId=$userId"
    ) {
        stationRepository
            .findByCustomerAndUserId(
                customerId = customerId,
                userId = userId
            )
            .map { it.toDto() }
    }

    fun getStationByUser(
        customerId: Int,
        userId: Int,
        stationId: Long
    ): StationDto = ServiceLogger.call(
        service = "station",
        operation = "getStationByUser",
        details = "customerId=$customerId userId=$userId stationId=$stationId"
    ) {
        stationRepository
            .findByCustomerUserAndId(
                customerId = customerId,
                userId = userId,
                stationId = stationId
            )
            ?.toDto()
            ?: error("Station not found")
    }

    fun createStation(
        command: CreateStationCommand
    ): StationDto = ServiceLogger.call(
        service = "station",
        operation = "createStation",
        details = "customerId=${command.customerId} name=${command.name.trim()}"
    ) {
        require(command.name.isNotBlank()) {
            "Station name must not be blank"
        }

        require(command.customerId > 0) {
            "Customer id must be positive"
        }

        stationRepository.create(
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
    ): List<StationDto> = ServiceLogger.call(
        service = "station",
        operation = "getStationsByMunicipality",
        details = "municipalityId=$municipalityId"
    ) {
        stationRepository
            .findByMunicipalityId(municipalityId)
            .map { it.toDto() }
    }
}