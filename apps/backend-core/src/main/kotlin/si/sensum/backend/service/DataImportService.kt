package si.sensum.backend.service

import si.sensum.backend.client.GmClient
import si.sensum.backend.repository.DataImportRepository
import si.sensum.geodsl.GeoDslProcessor
import si.sensum.geodsl.ast.ChannelNode
import si.sensum.geodsl.ast.MeasurementNode
import si.sensum.geodsl.ast.ProgramNode
import si.sensum.geodsl.ast.StationNode
import si.sensum.geodsl.export.export
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.shared.models.common.DataSourceDto
import si.sensum.shared.models.data.DataImportCommand
import si.sensum.shared.models.data.DataImportResult
import si.sensum.shared.models.data.DslImportCommand
import si.sensum.shared.models.data.SwsImportCommand
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand
import si.sensum.shared.models.measurements.StationChannelPairDto
import si.sensum.shared.models.stations.StationDto
import java.time.LocalDateTime
import java.time.ZoneId

class DataImportService(
    private val importRepository: DataImportRepository,
    private val gmClient: GmClient,
    private val swsUsername: String,
    private val swsPassword: String
) {
    fun importManual(command: DataImportCommand): DataImportResult = ServiceLogger.call(
        service = "data-import",
        operation = "importManual",
        details = "stations=${command.stations.size} channels=${command.channels.size} measurements=${command.measurements.size}"
    ) {
        importPlain(command.copy(source = DataSourceDto.MANUAL))
    }

    fun importSimulation(command: DataImportCommand): DataImportResult = ServiceLogger.call(
        service = "data-import",
        operation = "importSimulation",
        details = "stations=${command.stations.size} channels=${command.channels.size} measurements=${command.measurements.size}"
    ) {
        importPlain(command.copy(source = DataSourceDto.SIM))
    }

    fun importDsl(command: DslImportCommand): DataImportResult = ServiceLogger.call(
        service = "data-import",
        operation = "importDsl",
        details = "customerId=${command.customerId} userId=${command.userId}"
    ) {
        val program = GeoDslProcessor.parseString(command.source)
        val geoJson = export(program)

        val stations = program.toStationDtos()
        val channels = program.toChannelDtos()
        val measurements = program.toMeasurementDtos()

        val counts = importRepository.upsertAll(
            userId = command.userId,
            customerId = command.customerId,
            source = DataSourceDto.DSL,
            stations = stations,
            channels = channels,
            measurements = measurements
        )

        DataImportResult(
            source = DataSourceDto.DSL,
            stationCount = counts.stationCount,
            channelCount = counts.channelCount,
            measurementCount = counts.measurementCount,
            userStationCount = counts.userStationCount,
            stations = stations,
            channels = channels,
            measurements = measurements.take(100),
            geoJson = geoJson,
            message = "DSL processed and inserted into database."
        )
    }

    suspend fun importSwsStations(command: SwsImportCommand): DataImportResult =
        ServiceLogger.suspendCall(
            service = "data-import",
            operation = "importSwsStations",
            details = "customerId=${command.customerId} userId=${command.userId}"
        ) {
            val gmSessionToken = loginToGm()

            val stations = gmClient
                .getStations(gmSessionToken)
                .map { station -> station.copy(source = DataSourceDto.SWS) }

            val counts = importRepository.upsertAll(
                userId = command.userId,
                customerId = command.customerId,
                source = DataSourceDto.SWS,
                stations = stations,
                channels = emptyList(),
                measurements = emptyList()
            )

            DataImportResult(
                source = DataSourceDto.SWS,
                stationCount = counts.stationCount,
                userStationCount = counts.userStationCount,
                stations = stations,
                message = "SWS stations imported."
            )
        }

    suspend fun importSwsChannels(command: SwsImportCommand): DataImportResult =
        ServiceLogger.suspendCall(
            service = "data-import",
            operation = "importSwsChannels",
            details = "stationId=${command.stationId} customerId=${command.customerId} userId=${command.userId}"
        ) {
            val stationId = command.stationId
                ?: error("stationId is required for SWS channel import")

            val gmSessionToken = loginToGm()

            val channels = gmClient
                .getChannels(
                    gmSessionToken = gmSessionToken,
                    stationId = stationId
                )
                .map { channel -> channel.copy(source = DataSourceDto.SWS) }

            val counts = importRepository.upsertAll(
                userId = command.userId,
                customerId = command.customerId,
                source = DataSourceDto.SWS,
                stations = emptyList(),
                channels = channels,
                measurements = emptyList()
            )

            DataImportResult(
                source = DataSourceDto.SWS,
                channelCount = counts.channelCount,
                channels = channels,
                message = "SWS channels imported."
            )
        }

    suspend fun importSwsMeasurements(command: SwsImportCommand): DataImportResult =
        ServiceLogger.suspendCall(
            service = "data-import",
            operation = "importSwsMeasurements",
            details = "stationId=${command.stationId} channelId=${command.channelId}"
        ) {
            val stationId = command.stationId
                ?: error("stationId is required for SWS measurement import")

            val channelId = command.channelId
                ?: error("channelId is required for SWS measurement import")

            val datetimeFrom = command.datetimeFrom
                ?: error("datetimeFrom is required for SWS measurement import")

            val datetimeTo = command.datetimeTo
                ?: error("datetimeTo is required for SWS measurement import")

            val gmSessionToken = loginToGm()

            val measurements = gmClient
                .getMeasurements(
                    gmSessionToken = gmSessionToken,
                    request = RefreshMeasurementsCommand(
                        stationChannelPairs = listOf(
                            StationChannelPairDto(
                                stationId = stationId,
                                channelId = channelId
                            )
                        ),
                        datetimeFrom = datetimeFrom,
                        datetimeTo = datetimeTo
                    )
                )
                .map { measurement -> measurement.copy(source = DataSourceDto.SWS) }

            val counts = importRepository.upsertAll(
                userId = command.userId,
                customerId = command.customerId,
                source = DataSourceDto.SWS,
                stations = emptyList(),
                channels = emptyList(),
                measurements = measurements
            )

            DataImportResult(
                source = DataSourceDto.SWS,
                measurementCount = counts.measurementCount,
                measurements = measurements.take(100),
                message = "SWS measurements imported."
            )
        }

    private fun importPlain(command: DataImportCommand): DataImportResult {
        val counts = importRepository.upsertAll(
            userId = command.userId,
            customerId = command.customerId,
            source = command.source,
            stations = command.stations,
            channels = command.channels,
            measurements = command.measurements
        )

        return DataImportResult(
            source = command.source,
            stationCount = counts.stationCount,
            channelCount = counts.channelCount,
            measurementCount = counts.measurementCount,
            userStationCount = counts.userStationCount,
            stations = command.stations,
            channels = command.channels,
            measurements = command.measurements.take(100),
            message = "${command.source} data inserted into database."
        )
    }

    private suspend fun loginToGm(): String {
        require(swsUsername.isNotBlank()) {
            "Missing SWS_USERNAME"
        }

        require(swsPassword.isNotBlank()) {
            "Missing SWS_PASSWORD"
        }

        return gmClient
            .login(
                username = swsUsername,
                password = swsPassword
            )
            .token
    }

    private fun ProgramNode.toStationDtos(): List<StationDto> {
        return countries.flatMap { country ->
            country.stations.map { station ->
                StationDto(
                    stationId = station.id.toLong(),
                    name = station.name,
                    serialNumber = station.id.toString(),
                    description = "DSL station",
                    latitude = station.location.lat,
                    longitude = station.location.lon,
                    source = DataSourceDto.DSL
                )
            }
        }
    }

    private fun ProgramNode.toChannelDtos(): List<ChannelDto> {
        return countries.flatMap { country ->
            country.stations.flatMap { station ->
                station.channels.map { channel ->
                    channel.toDto(station)
                }
            }
        }
    }

    private fun ChannelNode.toDto(station: StationNode): ChannelDto {
        return ChannelDto(
            stationId = station.id.toLong(),
            channelId = id,
            name = name,
            description = kind.name.lowercase(),
            unit = unit,
            source = DataSourceDto.DSL
        )
    }

    private fun ProgramNode.toMeasurementDtos(): List<MeasurementDto> {
        return countries.flatMap { country ->
            country.stations.flatMap { station ->
                station.channels.flatMap { channel ->
                    channel.measurements.map { measurement ->
                        measurement.toDto(
                            station = station,
                            channel = channel
                        )
                    }
                }
            }
        }
    }

    private fun MeasurementNode.toDto(
        station: StationNode,
        channel: ChannelNode
    ): MeasurementDto {
        val local = LocalDateTime.parse(datetime)

        return MeasurementDto(
            id = null,
            stationId = station.id.toLong(),
            channelId = channel.id,
            dateTime = ApiDateTime.fromAppLocal(
                local.atZone(ZoneId.of("Europe/Ljubljana")).toLocalDateTime()
            ),
            value = value,
            status = when (status.name) {
                "OK" -> 0
                else -> 1
            },
            source = DataSourceDto.DSL
        )
    }
}