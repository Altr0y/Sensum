package si.sensum.demo.model

import si.sensum.shared.models.common.DataSourceDto
import java.time.LocalDateTime

data class MeasurementUi(
    val id: Int? = null,
    val stationId: Int,
    val stationName: String,
    val channelId: Int,
    val channelName: String,
    val dateTime: LocalDateTime,
    val value: Double,
    val status: Int,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)