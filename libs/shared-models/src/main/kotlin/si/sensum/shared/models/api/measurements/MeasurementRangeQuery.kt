package si.sensum.shared.models.api.measurements

import kotlinx.serialization.Serializable

@Serializable
data class MeasurementRangeQuery(
    val datetimeFrom: String,
    val datetimeTo: String
)