package si.sensum.backend.service

import si.sensum.geodsl.ast.ChannelKind
import si.sensum.geodsl.ast.StationNode
import si.sensum.shared.models.simulator.SimulateRequestDto
import si.sensum.shared.models.simulator.SimulatedChannelDto
import si.sensum.shared.models.simulator.SimulatedStationDto
import si.sensum.simulator.SimulateRequest
import si.sensum.simulator.SpatialSimulatorService
import java.time.LocalDateTime

class SimulatorService(
    private val spatialSimulatorService: SpatialSimulatorService
) {
    fun simulate(
        dto: SimulateRequestDto
    ): List<SimulatedStationDto> = ServiceLogger.call(
        service = "simulator",
        operation = "simulate",
        details = "mode=${dto.mode} count=${dto.count}"
    ) {
        val request = buildRequest(dto)

        val results = spatialSimulatorService.handle(request)

        results.map { context ->
            context.station.toDto(
                floodRisk = context.floodRisk,
                nearRiver = context.nearRiver
            )
        }
    }

    private fun buildRequest(dto: SimulateRequestDto): SimulateRequest {
        val from = dto.from
            ?.let { LocalDateTime.parse(it) }
            ?: LocalDateTime.now().minusMonths(1)

        val to = dto.to
            ?.let { LocalDateTime.parse(it) }
            ?: LocalDateTime.now()

        val kinds = dto.channelKinds.mapNotNull { kind ->
            parseChannelKind(kind)
        }

        return when (dto.mode) {
            "full" -> SimulateRequest.Full(
                count = dto.count,
                prefix = dto.prefix,
                channelKinds = kinds,
                regionName = dto.regionName,
                municipalityName = dto.municipalityName,
                from = from,
                to = to,
                intervalMinutes = dto.intervalMinutes
            )

            "stations_only" -> SimulateRequest.StationsOnly(
                count = dto.count,
                prefix = dto.prefix,
                regionName = dto.regionName,
                municipalityName = dto.municipalityName
            )

            "channels_only" -> SimulateRequest.ChannelsOnly(
                stationId = dto.stationId ?: error("stationId required for channels_only"),
                channelKinds = kinds
            )

            "measurements_only" -> SimulateRequest.MeasurementsOnly(
                stationId = dto.stationId ?: error("stationId required for measurements_only"),
                channelIds = dto.channelIds,
                from = from,
                to = to,
                intervalMinutes = dto.intervalMinutes
            )

            else -> error("Unknown mode: ${dto.mode}")
        }
    }

    private fun parseChannelKind(kind: String): ChannelKind? {
        return when (kind.lowercase()) {
            "water_level" -> ChannelKind.WATER_LEVEL
            "temperature" -> ChannelKind.TEMPERATURE
            "rainfall" -> ChannelKind.RAINFALL
            "flow_rate" -> ChannelKind.FLOW_RATE
            else -> null
        }
    }

    private fun StationNode.toDto(
        floodRisk: String?,
        nearRiver: Boolean
    ): SimulatedStationDto {
        return SimulatedStationDto(
            stationId = id,
            name = name,
            latitude = location.lat,
            longitude = location.lon,
            floodRisk = floodRisk,
            nearRiver = nearRiver,
            channels = channels.map { channel ->
                SimulatedChannelDto(
                    channelId = channel.id,
                    name = channel.name,
                    kind = channel.kind.name.lowercase(),
                    unit = channel.unit,
                    measurementCount = channel.measurements.size
                )
            }
        )
    }
}