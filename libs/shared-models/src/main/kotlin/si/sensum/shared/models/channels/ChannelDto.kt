package si.sensum.shared.models.channels

import kotlinx.serialization.Serializable
import si.sensum.shared.models.common.DataSourceDto

@Serializable
data class ChannelDto(
    val stationId: Long,
    val channelId: Int,
    val name: String? = null,
    val sampleTime: Long? = null,
    val alarmLow: Int? = null,
    val alarmHigh: Int? = null,
    val alarmHyst: Int? = null,
    val kota: Double? = null,
    val kotaSign: Double? = null,
    val description: String? = null,
    val unit: String? = null,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)