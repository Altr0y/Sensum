package si.sensum.demo.model

import java.time.LocalDateTime

data class Measurement(
    val id: Int? = null,
    val stationId: Int,
    val stationName: String,
    val channelId: Int,
    val channelName: String,
    val dateTime: LocalDateTime,
    val value: Double,
    val status: Int
)

data class StationChannelPair(
    val stationId: Int,
    val channelId: Int
)

data class MeasurementRequest(
    val pairs: List<StationChannelPair>,
    val datetimeFrom: LocalDateTime,
    val datetimeTo: LocalDateTime
)