package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.StationTable
import si.sensum.backend.domain.station.StationEntity
import si.sensum.backend.mapper.toDataSourceDto
import si.sensum.shared.models.common.DataSourceDto

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

    fun create(
        customerId: Int,
        name: String,
        latitude: Double,
        longitude: Double,
        description: String,
        serialNumber: String,
        source: DataSourceDto
    ): StationEntity = DatabaseTransaction.run {
        val id = StationTable.insert {
            it[StationTable.customerId] = customerId
            it[StationTable.alias] = name
            it[StationTable.latitude] = latitude
            it[StationTable.longitude] = longitude
            it[StationTable.locationDescription] = description
            it[StationTable.serialNumber] = serialNumber
            it[StationTable.dataSource] = source.name
            it[StationTable.location] = "$latitude,$longitude"
        } get StationTable.id

        findById(id)!!
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