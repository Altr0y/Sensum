package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.shared.models.channels.CreateChannelsRequest

internal class BackendChannelClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getChannels(): List<ChannelDto> {
        return backend.get("/api/v1/channels")
    }

    suspend fun getChannelById(channelId: Int): ChannelDto {
        return backend.get("/api/v1/channels/$channelId")
    }

    suspend fun getChannelsByStation(stationId: Long): List<ChannelDto> {
        return backend.get("/api/v1/stations/$stationId/channels")
    }

    suspend fun createChannels(stationId: Long, request: CreateChannelsRequest): List<ChannelDto> {
        return backend.post("/api/v1/stations/$stationId/channels", request)
    }
}