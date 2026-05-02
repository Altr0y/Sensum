package si.sensum.shared.models.api.measurements

import kotlinx.serialization.Serializable
import si.sensum.shared.models.serialization.OffsetDateTimeIsoSerializer
import java.time.OffsetDateTime

@Serializable
data class MeasurementDto(
    val stationId: Long,
    val channelId: Int,
    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val dateTime: OffsetDateTime,
    val value: Double,
    val status: Int
)