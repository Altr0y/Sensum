package si.sensum.sws.client

import io.ktor.client.HttpClient
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.StationChannelPairDto
import si.sensum.shared.models.stations.StationDto
import si.sensum.sws.SwsSoapExecutor
import si.sensum.sws.model.SwsChannelQuery
import si.sensum.sws.model.SwsDateTimeRange
import si.sensum.sws.model.SwsSession
import si.sensum.sws.model.SwsStationMeasurementQuery

/**
 * Public facade for the Smart Web Service SOAP API.
 *
 * External modules should use this class only.
 * SOAP operations, XML parsing and HTTP execution stay internal to sws-client.
 */
class SmartWebSoapClient(
    httpClient: HttpClient,
    baseUrl: String
) {
    private val soapExecutor = SwsSoapExecutor(
        httpClient = httpClient,
        baseUrl = baseUrl
    )

    private val operationExecutor = SwsOperationExecutor(
        soapExecutor = soapExecutor
    )

    private val authClient = SwsAuthClient(
        soapExecutor = soapExecutor
    )

    private val measurementsClient = SwsMeasurementClient(
        operationExecutor = operationExecutor
    )

    private val stationClient = SwsStationClient(
        operationExecutor = operationExecutor
    )

    private val channelClient = SwsChannelClient(
        operationExecutor = operationExecutor
    )

    suspend fun login(
        username: String,
        password: String
    ): SwsSession {
        return authClient.login(
            username = username,
            password = password
        )
    }

    suspend fun logout(session: SwsSession) {
        authClient.logout(session)
    }

    suspend fun getAllMeasurements(
        session: SwsSession,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return measurementsClient.getAllMeasurements(
            session = session,
            range = dateTimeRange(
                datetimeFrom = datetimeFrom,
                datetimeTo = datetimeTo
            )
        )
    }

    suspend fun getAllMeasurementsDetailed(
        session: SwsSession,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return measurementsClient.getAllMeasurementsDetailed(
            session = session,
            range = dateTimeRange(
                datetimeFrom = datetimeFrom,
                datetimeTo = datetimeTo
            )
        )
    }

    suspend fun getMeasurementsByStationChannelPairs(
        session: SwsSession,
        stationChannelPairs: List<StationChannelPairDto>,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return measurementsClient.getMeasurementsByStationChannelPairs(
            session = session,
            stationChannelPairs = stationChannelPairs,
            range = dateTimeRange(
                datetimeFrom = datetimeFrom,
                datetimeTo = datetimeTo
            )
        )
    }

    suspend fun getStations(session: SwsSession): List<StationDto> {
        return stationClient.getStations(session)
    }

    suspend fun getChannels(
        session: SwsSession,
        stationId: Long
    ): List<ChannelDto> {
        return channelClient.getChannels(
            session = session,
            stationId = stationId
        )
    }

    suspend fun getStationMeasurements(
        session: SwsSession,
        stationId: Long,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return measurementsClient.getStationMeasurements(
            session = session,
            query = SwsStationMeasurementQuery(
                stationId = stationId,
                range = dateTimeRange(
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            )
        )
    }

    suspend fun getModbusStationMeasurements(
        session: SwsSession,
        stationId: Long,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return measurementsClient.getModbusStationMeasurements(
            session = session,
            query = SwsStationMeasurementQuery(
                stationId = stationId,
                range = dateTimeRange(
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            )
        )
    }

    suspend fun getStationMeasurementsDetailed(
        session: SwsSession,
        stationId: Long,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return measurementsClient.getStationMeasurementsDetailed(
            session = session,
            query = SwsStationMeasurementQuery(
                stationId = stationId,
                range = dateTimeRange(
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            )
        )
    }

    suspend fun getChannelMeasurements(
        session: SwsSession,
        stationId: Long,
        channelId: Int,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return measurementsClient.getChannelMeasurements(
            session = session,
            query = SwsChannelQuery(
                stationId = stationId,
                channelId = channelId,
                range = dateTimeRange(
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            )
        )
    }

    suspend fun getChannelMeasurementsDetailed(
        session: SwsSession,
        stationId: Long,
        channelId: Int,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return measurementsClient.getChannelMeasurementsDetailed(
            session = session,
            query = SwsChannelQuery(
                stationId = stationId,
                channelId = channelId,
                range = dateTimeRange(
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            )
        )
    }

    private fun dateTimeRange(
        datetimeFrom: String,
        datetimeTo: String
    ): SwsDateTimeRange {
        return SwsDateTimeRange(
            from = datetimeFrom,
            to = datetimeTo
        )
    }
}