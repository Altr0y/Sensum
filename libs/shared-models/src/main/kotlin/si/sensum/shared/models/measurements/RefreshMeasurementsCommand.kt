package si.sensum.shared.models.measurements

import kotlinx.serialization.Serializable

@Serializable
data class RefreshMeasurementsCommand(
    val stationChannelPairs: List<StationChannelPairDto>,
    val datetimeFrom: String,
    val datetimeTo: String
)