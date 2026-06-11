package si.sensum.backend.domain.measurement

import kotlinx.datetime.LocalDateTime
import si.sensum.shared.models.common.DataSourceDto

data class MeasurementEntity(
    val id: Long = 0,
    val channelId: Int,
    val dateTime: LocalDateTime,
    val value: Float,
    val status: Boolean,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)