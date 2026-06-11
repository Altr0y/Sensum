package si.sensum.shared.models.simulator

import kotlinx.serialization.Serializable

@Serializable
data class SimulatedChannelDto(
    val channelId: Int,
    val name: String,
    val kind: String,
    val unit: String,
    val measurementCount: Int
)