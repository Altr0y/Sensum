package si.sensum.shared.models.measurements

import kotlinx.serialization.Serializable

@Serializable
data class CreateMeasurementsBatchCommand(
    val measurements: List<MeasurementDto>
)