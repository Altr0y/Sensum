package si.sensum.shared.models.measurements

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