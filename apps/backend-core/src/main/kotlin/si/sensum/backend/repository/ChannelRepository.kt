package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.ChannelTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.domain.channel.ChannelEntity
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