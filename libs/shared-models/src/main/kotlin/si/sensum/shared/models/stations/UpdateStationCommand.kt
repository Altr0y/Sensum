package si.sensum.shared.models.stations

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStationCommand(
    val name: String? = null,
    val description: String? = null,
    val serialNumber: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
