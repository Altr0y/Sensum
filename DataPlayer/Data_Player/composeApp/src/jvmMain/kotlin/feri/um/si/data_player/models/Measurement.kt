package feri.um.si.data_player.models

import java.time.LocalDateTime
import java.time.OffsetDateTime

//@Serializable
data class Measurement (
    val stationId: Long,
    val channelId: Int,
    val dateTime: OffsetDateTime,
    val value: Double,
    val status: Int
)

//station, channel models