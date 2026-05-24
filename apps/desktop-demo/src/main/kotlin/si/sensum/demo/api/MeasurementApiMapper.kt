package si.sensum.demo.api

import si.sensum.demo.model.DemoChannels
import si.sensum.demo.model.MeasurementUi
import si.sensum.shared.models.api.measurements.MeasurementDto
import java.time.ZoneOffset

fun MeasurementUi.toDto(): MeasurementDto {
    return MeasurementDto(
        id = id?.toLong(),
        stationId = stationId.toLong(),
        channelId = channelId,
        dateTime = dateTime.atOffset(ZoneOffset.UTC),
        value = value,
        status = status
    )
}

fun MeasurementDto.toUi(): MeasurementUi {
    return MeasurementUi(
        id = id?.toInt(),
        stationId = stationId.toInt(),
        stationName = DemoChannels.DEFAULT_STATION_NAME,
        channelId = channelId,
        channelName = DemoChannels.nameOf(channelId),
        dateTime = dateTime.toLocalDateTime(),
        value = value,
        status = status
    )
}