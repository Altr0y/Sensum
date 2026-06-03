package si.sensum.backend.domain.channel

import si.sensum.backend.domain.measurement.MeasurementUnit
import si.sensum.shared.models.common.DataSourceDto

data class ChannelEntity(
    val id: Int,
    val stationId: Long,
    val name: String,
    val description: String,
    val unit: MeasurementUnit,
    val enabled: Boolean,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)