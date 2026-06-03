package si.sensum.api.service

import si.sensum.api.client.BackendChannelClient
import si.sensum.shared.models.channels.ChannelDto

internal class ChannelService(
    private val channels: BackendChannelClient
) {
    suspend fun getChannels(): List<ChannelDto> {
        return channels.getChannels()
    }

    suspend fun getChannelById(channelId: Int): ChannelDto {
        require(channelId > 0) {
            "Channel id must be positive"
        }

        return channels.getChannelById(channelId)
    }

    suspend fun getChannelsByStation(stationId: Long): List<ChannelDto> {
        require(stationId > 0) {
            "Station id must be positive"
        }

        return channels.getChannelsByStation(stationId)
    }
}