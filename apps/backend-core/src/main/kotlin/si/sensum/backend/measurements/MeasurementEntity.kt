package si.sensum.backend.measurements

import kotlinx.datetime.LocalDateTime

data class MeasurementEntity(
    val id: Long = 0,
    val channelId: Int,
    val dateTime: LocalDateTime,
    val value: Float,
    val status: Boolean
)