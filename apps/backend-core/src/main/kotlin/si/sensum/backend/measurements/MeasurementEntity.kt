package si.sensum.backend.measurements

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MeasurementEntity(
    val id: Long,
    val channelId: Int,
    val dateTime: LocalDateTime,
    val value: Float,
    val status: Boolean
)
