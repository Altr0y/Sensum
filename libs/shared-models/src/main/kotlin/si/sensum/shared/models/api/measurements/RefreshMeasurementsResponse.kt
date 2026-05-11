package si.sensum.shared.models.api.measurements

import kotlinx.serialization.Serializable

@Serializable
data class RefreshMeasurementsResponse(
    val deletedCount: Int,
    val insertedCount: Int
)