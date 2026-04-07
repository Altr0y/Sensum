package feri.um.si.data_player.models

import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class Measurement (
    val stationId: Long,
    val channelId: Int,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateTime: OffsetDateTime,
    val value: Double,
    val status: Int
)

//TODO: station, channel?