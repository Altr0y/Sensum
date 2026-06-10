package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.ChannelTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.domain.channel.ChannelEntity
import si.sensum.backend.domain.measurement.MeasurementUnit
import si.sensum.backend.mapper.toDataSourceDto

class ChannelRepository {

    fun findAll(): List<ChannelEntity> = DatabaseTransaction.run {
        ChannelTable
            .selectAll()
            .map(::toChannel)
    }

    fun findById(channelId: Int): ChannelEntity? = DatabaseTransaction.run {
        ChannelTable
            .selectAll()
            .where { ChannelTable.id eq channelId }
            .map(::toChannel)
            .singleOrNull()
    }

    fun findByStationId(stationId: Long): List<ChannelEntity> = DatabaseTransaction.run {
        ChannelTable
            .selectAll()
            .where { ChannelTable.stationId eq stationId }
            .map(::toChannel)
    }

    fun create(
        stationId: Long,
        name: String,
        unit: MeasurementUnit,
        source: String = "SIM"
    ): ChannelEntity = DatabaseTransaction.run {
        val id = ChannelTable.insert {
            it[ChannelTable.stationId] = stationId
            it[ChannelTable.name] = name
            it[ChannelTable.description] = ""
            it[ChannelTable.unit] = unit
            it[ChannelTable.enabled] = true
            it[ChannelTable.dataSource] = source
        } get ChannelTable.id

        findById(id)!!
    }

    private fun toChannel(row: ResultRow): ChannelEntity {
        return ChannelEntity(
            id = row[ChannelTable.id],
            stationId = row[ChannelTable.stationId],
            name = row[ChannelTable.name],
            description = row[ChannelTable.description],
            unit = row[ChannelTable.unit],
            enabled = row[ChannelTable.enabled],
            source = row[ChannelTable.dataSource].toDataSourceDto()
        )
    }
}