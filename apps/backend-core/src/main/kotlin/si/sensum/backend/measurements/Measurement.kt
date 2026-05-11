package si.sensum.backend.measurements

import kotlinx.datetime.LocalDateTime

data class Measurement(
    val id: Int? = null,
    val stationId: Long,
    val stationName: String,
    val channelId: Int,
    val channelName: String,
    val dateTime: LocalDateTime,
    val value: Double,
    val status: Int
)