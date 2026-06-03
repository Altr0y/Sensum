package si.sensum.shared.models.simulator

import kotlinx.serialization.Serializable

@Serializable
data class SimulateRequestDto(
    val mode: String,              // "full", "stations_only", "channels_only", "measurements_only"
    val count: Int = 1,
    val prefix: String = "Postaja",
    val channelKinds: List<String> = emptyList(), // "water_level", "temperature", "rainfall", "flow_rate"
    val regionName: String? = null,
    val municipalityName: String? = null,
    val stationId: Int? = null,
    val channelIds: List<Int> = emptyList(),
    val from: String? = null,      // ISO datetime npr. "2026-01-01T00:00:00"
    val to: String? = null,
    val intervalMinutes: Long = 60L
)