package si.sensum.shared.models.api.measurements

import kotlinx.serialization.Serializable

@Serializable
data class MeasurementsByStationChannelPairsResponse(
    val deletedCount: Int,
    val insertedCount: Int
)