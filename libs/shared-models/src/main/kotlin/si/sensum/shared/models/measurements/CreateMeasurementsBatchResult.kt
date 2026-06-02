package si.sensum.shared.models.measurements

import kotlinx.serialization.Serializable

@Serializable
data class CreateMeasurementsBatchResult(
    val insertedCount: Int
)