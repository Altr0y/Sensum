package si.sensum.shared.models.records

import kotlinx.serialization.Serializable
import si.sensum.shared.models.common.DataSourceDto

@Serializable
data class ChannelRecordDto(
    val channelId: Int,
    val stationId: Long,
    val name: String? = null,
    val unit: String? = null,
    val description: String? = null,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)