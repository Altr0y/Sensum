package si.sensum.demo.api

import si.sensum.demo.model.DemoChannels
import si.sensum.demo.model.MeasurementUi
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.shared.models.measurements.MeasurementDto

fun MeasurementUi.toDto(): MeasurementDto {
    return MeasurementDto(
        id = id?.toLong(),
        stationId = stationId.toLong(),
        channelId = channelId,
        dateTime = ApiDateTime.fromAppLocal(dateTime),
        value = value,
        status = status,
        source = source
    )
}

fun MeasurementDto.toUi(): MeasurementUi {
    return MeasurementUi(
        id = id?.toInt(),
        stationId = stationId.toInt(),
        stationName = DemoChannels.DEFAULT_STATION_NAME,
        channelId = channelId,
        channelName = DemoChannels.nameOf(channelId),
        dateTime = ApiDateTime.toAppLocal(dateTime),
        value = value,
        status = status,
        source = source
    )
}