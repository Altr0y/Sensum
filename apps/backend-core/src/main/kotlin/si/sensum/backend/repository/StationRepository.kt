package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.StationTable
import si.sensum.backend.database.UserStationTable
import si.sensum.backend.domain.station.StationEntity
import si.sensum.backend.mapper.toDataSourceDto
import si.sensum.shared.models.common.DataSourceDto
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.ChannelTable
import si.sensum.backend.database.MeasurementTable

class StationRepository {

    // all stations
    fun findAll(): List<StationEntity> = DatabaseTransaction.run {
        StationTable
            .selectAll()
            .map(::toStation)
    }

    // single station by id
    fun findById(stationId: Long): StationEntity? = DatabaseTransaction.run {
        StationTable
            .selectAll()
            .where { StationTable.id eq stationId }
            .map(::toStation)
            .singleOrNull()
    }

    // all stations by customer id
    fun findByCustomerId(customerId: Int): List<StationEntity> = DatabaseTransaction.run {
        StationTable
            .selectAll()
            .where { StationTable.customerId eq customerId }
            .map(::toStation)
    }

    // single station by customer and station id
    fun findByCustomerAndId(
        customerId: Int,
        stationId: Long
    ): StationEntity? = DatabaseTransaction.run {
        StationTable
            .selectAll()
            .where {
                (StationTable.customerId eq customerId) and
                        (StationTable.id eq stationId)
            }
            .map(::toStation)
            .singleOrNull()
    }

    // all stations by customer and user id
    fun findByCustomerAndUserId(
        customerId: Int,
        userId: Int
    ): List<StationEntity> = DatabaseTransaction.run {
        (StationTable innerJoin UserStationTable)
            .select(StationTable.columns)
            .where {
                (StationTable.customerId eq customerId) and
                        (UserStationTable.userId eq userId)
            }
            .map(::toStation)
    }

    // single station by customer, user and station id
    fun findByCustomerUserAndId(
        customerId: Int,
        userId: Int,
        stationId: Long
    ): StationEntity? = DatabaseTransaction.run {
        (StationTable innerJoin UserStationTable)
            .select(StationTable.columns)
            .where {
                (StationTable.customerId eq customerId) and
                        (UserStationTable.userId eq userId) and
                        (StationTable.id eq stationId)
            }
            .map(::toStation)
            .singleOrNull()
    }

    // all stations by municipality id
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

    fun delete(stationId: Long): Boolean = DatabaseTransaction.run {
        // najprej zbriši meritve za vse kanale te postaje
        val channelIds = ChannelTable
            .selectAll()
            .where { ChannelTable.stationId eq stationId }
            .map { it[ChannelTable.id] }

        if (channelIds.isNotEmpty()) {
            MeasurementTable.deleteWhere {
                MeasurementTable.channelId inList channelIds
            }
            ChannelTable.deleteWhere {
                ChannelTable.stationId eq stationId
            }
        }

        UserStationTable.deleteWhere {
            UserStationTable.stationId eq stationId
        }

        StationTable.deleteWhere {
            StationTable.id eq stationId
        } > 0
    }

    fun update(
        stationId: Long,
        name: String?,
        description: String?,
        serialNumber: String?,
        latitude: Double?,
        longitude: Double?
    ): StationEntity? = DatabaseTransaction.run {
        val current = findById(stationId) ?: return@run null

        val newLatitude = latitude ?: current.latitude
        val newLongitude = longitude ?: current.longitude

        StationTable.update({ StationTable.id eq stationId }) {
            name?.let { v -> it[StationTable.alias] = v }
            description?.let { v -> it[StationTable.locationDescription] = v }
            serialNumber?.let { v -> it[StationTable.serialNumber] = v }
            if (latitude != null) it[StationTable.latitude] = latitude
            if (longitude != null) it[StationTable.longitude] = longitude
            if (latitude != null || longitude != null) {
                it[StationTable.location] = "$newLatitude,$newLongitude"
            }
        }

        findById(stationId)
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