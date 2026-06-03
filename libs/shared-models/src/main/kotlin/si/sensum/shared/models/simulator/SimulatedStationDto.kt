package si.sensum.shared.models.simulator

import kotlinx.serialization.Serializable

@Serializable
data class SimulatedStationDto(
    val stationId: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val floodRisk: String?,
    val nearRiver: Boolean,
    val channels: List<SimulatedChannelDto>
)