package si.sensum.shared.models.api.measurements

import kotlinx.serialization.Serializable

@Serializable
data class MeasurementsByStationChannelPairsRequest(
    val stationChannelPairs: List<StationChannelPairDto>,
    val datetimeFrom: String,
    val datetimeTo: String
)