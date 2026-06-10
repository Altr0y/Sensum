package si.sensum.backend.service

import si.sensum.backend.repository.ChannelRepository
import si.sensum.backend.repository.MeasurementRepository
import si.sensum.backend.repository.StationRepository
import si.sensum.geodsl.ast.ChannelKind
import si.sensum.geodsl.ast.StationNode
import si.sensum.shared.models.simulator.SimulateRequestDto
import si.sensum.shared.models.simulator.SimulatedChannelDto
import si.sensum.shared.models.simulator.SimulatedStationDto
import si.sensum.simulator.SimulateRequest
import si.sensum.simulator.SpatialSimulatorService
import java.time.LocalDateTime
import si.sensum.backend.domain.measurement.MeasurementEntity
import si.sensum.backend.domain.measurement.MeasurementUnit
import si.sensum.backend.mapper.toDataSourceDto
import si.sensum.shared.models.common.DataSourceDto
import kotlinx.datetime.toKotlinLocalDateTime
import si.sensum.geodsl.ast.PointNode

class SimulatorService(
    private val spatialSimulatorService: SpatialSimulatorService,
    private val stationRepository: StationRepository,
    private val channelRepository: ChannelRepository,
    private val measurementRepository: MeasurementRepository
) {
    fun simulate(
        dto: SimulateRequestDto
    ): List<SimulatedStationDto> = ServiceLogger.call(
        service = "simulator",
        operation = "simulate",
        details = "mode=${dto.mode} count=${dto.count} from=${dto.from} to=${dto.to}"
    ) {
        val request = buildRequest(dto)

        spatialSimulatorService
            .handle(request)
            .map { context ->
                context.station.toDto(
                    floodRisk = context.floodRisk,
                    nearRiver = context.nearRiver
                )
            }
    }

    fun simulateAndSave(
        dto: SimulateRequestDto,
        customerId: Int
    ): List<SimulatedStationDto> = ServiceLogger.call(
        service = "simulator",
        operation = "simulateAndSave",
        details = "mode=${dto.mode} count=${dto.count} from=${dto.from} to=${dto.to}"
    ) {
        val request = buildRequest(dto)
        val contexts = spatialSimulatorService.handle(request)

        contexts.map { context ->
            val station = context.station

            // shrani postajo
            val savedStation = stationRepository.create(
                customerId = customerId,
                name = station.name,
                latitude = station.location.lat,
                longitude = station.location.lon,
                description = "",
                serialNumber = "SIM-${station.id}",
                source = DataSourceDto.SIM
            )

            // shrani kanale in meritve
            station.channels.forEach { channel ->
                val unit = when (channel.kind) {
                    si.sensum.geodsl.ast.ChannelKind.TEMPERATURE -> MeasurementUnit.CELSIUS
                    si.sensum.geodsl.ast.ChannelKind.WATER_LEVEL -> MeasurementUnit.METERS
                    si.sensum.geodsl.ast.ChannelKind.RAINFALL -> MeasurementUnit.METERS
                    si.sensum.geodsl.ast.ChannelKind.FLOW_RATE -> MeasurementUnit.METERS
                }

                val savedChannel = channelRepository.create(
                    stationId = savedStation.id,
                    name = channel.name,
                    unit = unit,
                    source = "SIM"
                )

                val measurements = channel.measurements.map { m ->
                    MeasurementEntity(
                        id = 0,
                        channelId = savedChannel.id,
                        dateTime = LocalDateTime.parse(m.datetime).toKotlinLocalDateTime(),
                        value = m.value.toFloat(),
                        status = false,
                        source = DataSourceDto.SIM
                    )
                }

                measurementRepository.batchInsert(measurements)
            }

            station.toDto(
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

        require(!to.isBefore(from)) {
            "Field 'to' must be after or equal to field 'from'"
        }

        val kinds = dto.channelKinds
            .ifEmpty {
                listOf(
                    "water_level",
                    "temperature",
                    "rainfall",
                    "flow_rate"
                )
            }
            .mapNotNull { parseChannelKind(it) }

        return when (dto.mode) {
            "full" -> SimulateRequest.Full(
                count = dto.count,
                prefix = dto.prefix,
                channelKinds = kinds,
                regionName = dto.regionName,
                municipalityName = dto.municipalityName,
                from = from,
                to = to,
                intervalMinutes = dto.intervalMinutes,
                polygon = dto.polygon?.map { coords ->
                    PointNode(lon = coords[0], lat = coords[1])
                }
            )

            "stations_only" -> SimulateRequest.StationsOnly(
                count = dto.count,
                prefix = dto.prefix,
                regionName = dto.regionName,
                municipalityName = dto.municipalityName
            )

            "channels_only" -> SimulateRequest.ChannelsOnly(
                stationId = dto.stationId
                    ?: error("stationId required for channels_only"),
                channelKinds = kinds
            )

            "measurements_only" -> SimulateRequest.MeasurementsOnly(
                stationId = dto.stationId
                    ?: error("stationId required for measurements_only"),
                channelIds = dto.channelIds,
                from = from,
                to = to,
                intervalMinutes = dto.intervalMinutes
            )

            else -> error("Unknown simulator mode: ${dto.mode}")
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