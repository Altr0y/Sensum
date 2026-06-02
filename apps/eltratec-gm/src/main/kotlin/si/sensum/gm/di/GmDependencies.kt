package si.sensum.gm.di

import si.sensum.gm.config.GmAppConfig
import si.sensum.gm.config.createHttpClient
import si.sensum.gm.services.AuthService
import si.sensum.gm.services.ChannelService
import si.sensum.gm.services.MeasurementService
import si.sensum.gm.services.StationService
import si.sensum.gm.model.GmUserSession
import si.sensum.shared.auth.store.InMemorySessionStore
import si.sensum.shared.auth.service.TokenService
import si.sensum.sws.client.SmartWebSoapClient
import java.time.Duration

internal class GmDependencies(
    val authService: AuthService,
    val measurementService: MeasurementService,
    val stationService: StationService,
    val channelService: ChannelService
)

internal fun createGmDependencies(config: GmAppConfig): GmDependencies {
    val sessionStore = InMemorySessionStore<GmUserSession>(
        expiresAt = { session -> session.expiresAt }
    )

    val tokenService = TokenService(
        ttl = Duration.ofHours(config.auth.tokenTtlHours)
    )

    val httpClient = createHttpClient(
        timeoutMillis = config.sws.timeoutMillis
    )

    val swsClient = SmartWebSoapClient(
        httpClient = httpClient,
        baseUrl = config.sws.baseUrl
    )

    val authService = AuthService(
        soapClient = swsClient,
        tokenService = tokenService,
        sessionStore = sessionStore
    )

    val measurementService = MeasurementService(
        soapClient = swsClient
    )

    val stationService = StationService(
        soapClient = swsClient
    )

    val channelService = ChannelService(
        soapClient = swsClient
    )

    return GmDependencies(
        authService = authService,
        measurementService = measurementService,
        stationService = stationService,
        channelService = channelService
    )
}