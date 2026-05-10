package si.sensum.backend.measurements

import kotlinx.datetime.LocalDateTime

data class Measurement(
    val id: Long,
    val channelId: Int,
    val date: LocalDateTime,
    val value: Float,
    val status: Boolean
)
