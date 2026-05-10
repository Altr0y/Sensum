package si.sensum.gm.di

import si.sensum.gm.auth.StaticGmTokenValidator
import si.sensum.gm.config.GmAppConfig
import si.sensum.gm.config.createHttpClient
import si.sensum.gm.services.AuthService
import si.sensum.gm.services.MeasurementService
import si.sensum.shared.auth.service.TokenService
import si.sensum.shared.auth.store.InMemorySessionStore
import si.sensum.sws.SmartWebSoapClient
import java.time.Duration

class GmDependencies(
    val authService: AuthService,
    val measurementService: MeasurementService,
    val gmTokenValidator: StaticGmTokenValidator
)

fun createGmDependencies(config: GmAppConfig): GmDependencies {
    val sessionStore = InMemorySessionStore()

    val tokenService = TokenService(
        ttl = Duration.ofHours(config.auth.tokenTtlHours)
    )

    val gmTokenValidator = StaticGmTokenValidator(
        expectedToken = config.auth.apiToken
    )

    val httpClient = createHttpClient(
        timeoutMillis = config.sws.timeoutMillis
    )

    val soapClient = SmartWebSoapClient(
        httpClient = httpClient,
        baseUrl = config.sws.baseUrl
    )

    val authService = AuthService(
        soapClient = soapClient,
        tokenService = tokenService,
        sessionStore = sessionStore
    )

    val measurementService = MeasurementService(
        soapClient = soapClient
    )

    return GmDependencies(
        authService = authService,
        measurementService = measurementService,
        gmTokenValidator = gmTokenValidator
    )
}