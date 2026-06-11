package si.sensum.shared.models.stations

import kotlinx.serialization.Serializable
import si.sensum.shared.models.common.DataSourceDto

@Serializable
data class CreateStationCommand(
    val customerId: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String = "",
    val serialNumber: String = "",
    val source: DataSourceDto = DataSourceDto.MANUAL
)
