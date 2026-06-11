package si.sensum.shared.models.records

import kotlinx.serialization.Serializable
import si.sensum.shared.models.common.DataSourceDto

@Serializable
data class MeasurementRecordDto(
    val id: Long? = null,
    val stationId: Long,
    val stationName: String? = null,
    val channelId: Int,
    val channelName: String? = null,
    val dateTime: String,
    val value: Double,
    val status: Int,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)