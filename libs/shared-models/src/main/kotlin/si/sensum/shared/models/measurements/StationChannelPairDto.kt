package si.sensum.shared.models.measurements

import kotlinx.serialization.Serializable

@Serializable
data class StationChannelPairDto(
    val stationId: Long,
    val channelId: Int
)