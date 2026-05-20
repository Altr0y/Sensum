package si.sensum.simulator.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationConfig(
    @SerialName("station_id") val stationId: Int,
    @SerialName("station_name") val stationName: String,
    @SerialName("real_data_range") val realDataRange: RealDataRange,
    @SerialName("measurement_interval_minutes") val measurementIntervalMinutes: Int = 1,
    val channels: List<ChannelConfig>
)

@Serializable
data class RealDataRange(
    val from: String,
    val to: String
)