package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.ChannelRepository
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.backend.domain.measurement.MeasurementUnit
import si.sensum.shared.models.channels.CreateChannelsRequest

class ChannelService(
    private val channelRepository: ChannelRepository
) {
    fun getAllChannels(): List<ChannelDto> = ServiceLogger.call(
        service = "channel",
        operation = "getAllChannels"
    ) {
        channelRepository
            .findAll()
            .map { it.toDto() }
    }

    fun getChannel(
        channelId: Int
    ): ChannelDto = ServiceLogger.call(
        service = "channel",
        operation = "getChannel",
        details = "channelId=$channelId"
    ) {
        channelRepository
            .findById(channelId)
            ?.toDto()
            ?: error("Channel not found")
    }

    fun getChannelsByStation(
        stationId: Long
    ): List<ChannelDto> = ServiceLogger.call(
        service = "channel",
        operation = "getChannelsByStation",
        details = "stationId=$stationId"
    ) {
        channelRepository
            .findByStationId(stationId)
            .map { it.toDto() }
    }

    fun createChannels(
        stationId: Long,
        request: CreateChannelsRequest
    ): List<ChannelDto> = ServiceLogger.call(
        service = "channel",
        operation = "createChannels",
        details = "stationId=$stationId kinds=${request.kinds}"
    ) {
        request.kinds.map { kind ->
            val (name, unit) = when (kind.lowercase()) {
                "water_level" -> "Water level" to MeasurementUnit.METERS
                "temperature" -> "Temperature" to MeasurementUnit.CELSIUS
                "rainfall" -> "Rainfall" to MeasurementUnit.METERS
                "flow_rate" -> "Flow rate" to MeasurementUnit.METERS
                else -> error("Unknown channel kind: $kind")
            }
            channelRepository.create(
                stationId = stationId,
                name = name,
                unit = unit,
                source = "SIM"
            ).toDto()
        }
    }
}