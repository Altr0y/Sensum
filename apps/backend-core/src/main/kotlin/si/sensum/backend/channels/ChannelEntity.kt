package si.sensum.backend.channels

import si.sensum.backend.measurements.MeasurementUnit

data class ChannelEntity(
    val id: Int,
    val stationId: Long,
    val name: String,
    val description: String,
    val unit: MeasurementUnit,
    val enabled: Boolean
)
