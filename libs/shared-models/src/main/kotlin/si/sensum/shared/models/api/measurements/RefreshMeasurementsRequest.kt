package si.sensum.shared.models.api.measurements

import kotlinx.serialization.Serializable

@Serializable
data class RefreshMeasurementsRequest(
    val stationChannelPairs: List<StationChannelPairDto>,
    val datetimeFrom: String,
    val datetimeTo: String
)

@Serializable
data class StationChannelPairDto(
    val stationId: Long,
    val channelId: Int
)