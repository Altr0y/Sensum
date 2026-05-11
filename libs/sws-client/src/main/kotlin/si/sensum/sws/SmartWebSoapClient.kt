package si.sensum.sws

import io.ktor.client.HttpClient
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.sws.auth.SwsAuthClient
import si.sensum.sws.measurements.SwsMeasurementsClient
import si.sensum.sws.model.SwsSession
import si.sensum.shared.models.api.measurements.StationChannelPairDto

class SmartWebSoapClient(
    httpClient: HttpClient,
    baseUrl: String
) {

    private val soapExecutor = SwsSoapExecutor(
        httpClient = httpClient,
        baseUrl = baseUrl
    )

    private val authClient = SwsAuthClient(
        soapExecutor = soapExecutor
    )

    private val measurementsClient = SwsMeasurementsClient(
        soapExecutor = soapExecutor
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
            datetimeFrom = datetimeFrom,
            datetimeTo = datetimeTo
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
            datetimeFrom = datetimeFrom,
            datetimeTo = datetimeTo
        )
    }

    suspend fun getAllMeasurementsDetailed(
        session: SwsSession,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return measurementsClient.getAllMeasurementsDetailed(
            session = session,
            datetimeFrom = datetimeFrom,
            datetimeTo = datetimeTo
        )
    }
}