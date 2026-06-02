package si.sensum.backend.domain.channel

import si.sensum.backend.domain.measurement.MeasurementUnit

data class ChannelEntity(
    val id: Int,
    val stationId: Long,
    val name: String,
    val description: String,
    val unit: MeasurementUnit,
    val enabled: Boolean
)