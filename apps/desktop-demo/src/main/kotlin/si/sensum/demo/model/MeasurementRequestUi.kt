package si.sensum.demo.model

import java.time.LocalDateTime

data class MeasurementRequestUi(
    val pairs: List<StationChannelPairUi>,
    val datetimeFrom: LocalDateTime,
    val datetimeTo: LocalDateTime
)