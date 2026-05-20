package si.sensum.api.di

import si.sensum.api.backend.BackendAuthClient
import si.sensum.api.backend.BackendChannelClient
import si.sensum.shared.http.ServiceHttpClient
import si.sensum.api.backend.BackendMeasurementClient
import si.sensum.api.backend.BackendStationClient
import si.sensum.api.config.ApiGatewayConfig
import si.sensum.api.config.createHttpClient
import si.sensum.api.services.AuthService
import si.sensum.shared.auth.jwt.JwtConfig
import si.sensum.shared.auth.jwt.JwtTokenService

internal fun createApiDependencies(
    config: ApiGatewayConfig
): ApiDependencies {
    val jwtTokenService = JwtTokenService(
        config = JwtConfig(
            secret = config.jwtSecret,
            issuer = config.jwtIssuer,
            audience = config.jwtAudience,
            ttlSeconds = config.jwtTtlSeconds
        )
    )

    val httpClient = createHttpClient()

    val backendHttpClient = ServiceHttpClient(
        httpClient = httpClient,
        baseUrl = config.backendCoreBaseUrl
    )

    val backendAuth = BackendAuthClient(backendHttpClient)

    val authService = AuthService(
        backendAuthClient = backendAuth,
        jwtTokenService = jwtTokenService
    )

    return ApiDependencies(
        jwtTokenService = jwtTokenService,
        authService = authService,
        backendMeasurements = BackendMeasurementClient(backendHttpClient),
        backendStations = BackendStationClient(backendHttpClient),
        backendChannels = BackendChannelClient(backendHttpClient)
    )
}