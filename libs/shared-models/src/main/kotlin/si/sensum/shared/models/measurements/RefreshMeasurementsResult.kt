package si.sensum.shared.models.measurements

import kotlinx.serialization.Serializable

@Serializable
data class RefreshMeasurementsResult(
    val deletedCount: Int,
    val insertedCount: Int
)