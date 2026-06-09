package si.sensum.demo.api

import si.sensum.demo.model.MeasurementUi
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.stations.StationDto

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

fun MeasurementDto.toUi(
    stations: List<StationDto> = emptyList(),
    channels: List<ChannelDto> = emptyList()
): MeasurementUi {
    val station = stations.firstOrNull { item ->
        item.stationId == stationId
    }

    val channel = channels.firstOrNull { item ->
        item.channelId == channelId
    }

    return MeasurementUi(
        id = id?.toInt(),
        stationId = stationId.toInt(),
        stationName = station?.name ?: "Station $stationId",
        channelId = channelId,
        channelName = channel?.name ?: "Channel $channelId",
        dateTime = ApiDateTime.toAppLocal(dateTime),
        value = value,
        status = status,
        source = source
    )
}

fun MeasurementDto.toMeasurementUi(): MeasurementUi {
    return toUi()
}