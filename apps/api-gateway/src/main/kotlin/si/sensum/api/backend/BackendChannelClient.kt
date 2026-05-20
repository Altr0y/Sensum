package si.sensum.api.backend

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.api.channels.ChannelDto

internal class BackendChannelClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getChannels(stationId: Long): List<ChannelDto> {
        return backend.get("/api/v1/stations/$stationId/channels")
    }
}