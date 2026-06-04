package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.StationTable
import si.sensum.backend.domain.station.StationEntity
import si.sensum.backend.mapper.toDataSourceDto

class StationRepository {

    fun findAll(): List<StationEntity> = DatabaseTransaction.run {
        StationTable
            .selectAll()
            .map(::toStation)
    }

    fun findById(stationId: Long): StationEntity? = DatabaseTransaction.run {
        StationTable
            .selectAll()
            .where { StationTable.id eq stationId }
            .map(::toStation)
            .singleOrNull()
    }

    fun findByCustomerId(customerId: Int): List<StationEntity> = DatabaseTransaction.run {
        StationTable
            .selectAll()
            .where { StationTable.customerId eq customerId }
            .map(::toStation)
    }

    fun findByMunicipalityId(municipalityId: Int): List<StationEntity> = DatabaseTransaction.run {
        StationTable
            .selectAll()
            .where { StationTable.municipalityId eq municipalityId }
            .map(::toStation)
    }

    private fun toStation(row: ResultRow): StationEntity {
        return StationEntity(
            id = row[StationTable.id],
            customerId = row[StationTable.customerId],
            countryId = row[StationTable.countryId],
            regionId = row[StationTable.regionId],
            municipalityId = row[StationTable.municipalityId],
            locationDescription = row[StationTable.locationDescription],
            alias = row[StationTable.alias],
            serialNumber = row[StationTable.serialNumber],
            longitude = row[StationTable.longitude],
            latitude = row[StationTable.latitude],
            location = row[StationTable.location],
            source = row[StationTable.dataSource].toDataSourceDto()
        )
    }
}