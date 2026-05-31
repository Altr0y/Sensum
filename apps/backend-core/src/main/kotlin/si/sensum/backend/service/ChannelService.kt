package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.ChannelRepository
import si.sensum.shared.models.channels.ChannelDto

class ChannelService(
    private val channelRepository: ChannelRepository
) {
    fun getAllChannels(): List<ChannelDto> {
        return channelRepository
            .findAll()
            .map { it.toDto() }
    }

    fun getChannel(
        channelId: Int
    ): ChannelDto {
        return channelRepository
            .findById(channelId)
            ?.toDto()
            ?: error("Channel not found")
    }

    fun getChannelsByStation(
        stationId: Long
    ): List<ChannelDto> {
        return channelRepository
            .findByStationId(stationId)
            .map { it.toDto() }
    }
}