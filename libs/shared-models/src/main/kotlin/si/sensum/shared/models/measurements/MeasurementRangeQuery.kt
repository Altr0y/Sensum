package si.sensum.shared.models.measurements

import kotlinx.serialization.Serializable

@Serializable
data class MeasurementRangeQuery(
    val datetimeFrom: String,
    val datetimeTo: String
)