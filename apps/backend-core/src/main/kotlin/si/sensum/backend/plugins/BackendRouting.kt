package si.sensum.backend.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import si.sensum.backend.client.GmClient
import si.sensum.backend.config.BackendConfig
import si.sensum.backend.config.createHttpClient
import si.sensum.backend.controller.*
import si.sensum.backend.repository.*
import si.sensum.backend.service.*
import si.sensum.shared.auth.jwt.JwtConfig
import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.http.ServiceHttpClient

fun Application.configureBackendRouting(
    backendConfig: BackendConfig
) {
    val httpClient = createHttpClient()

    val gmServiceJwtTokenService = JwtTokenService(
        config = JwtConfig(
            secret = backendConfig.gmServiceJwtSecret,
            issuer = backendConfig.gmServiceJwtIssuer,
            audience = backendConfig.gmServiceJwtAudience,
            ttlSeconds = backendConfig.gmServiceJwtTtlSeconds
        )
    )

    val gmHttpClient = ServiceHttpClient(
        httpClient = httpClient,
        baseUrl = backendConfig.gmBaseUrl
    )

    val gmClient = GmClient(
        serviceHttpClient = gmHttpClient,
        serviceJwtTokenService = gmServiceJwtTokenService
    )

    val customerRepository = CustomerRepository()
    val userRepository = UserRepository()
    val stationRepository = StationRepository()
    val channelRepository = ChannelRepository()
    val measurementRepository = MeasurementRepository()

    val authService = AuthService(
        userRepository = userRepository
    )

    val customerService = CustomerService(
        customerRepository = customerRepository
    )

    val userService = UserService(
        userRepository = userRepository
    )

    val stationService = StationService(
        stationRepository = stationRepository
    )

    val channelService = ChannelService(
        channelRepository = channelRepository
    )

    val measurementService = MeasurementService(
        repository = measurementRepository
    )

    val measurementRefreshService = MeasurementRefreshService(
        gmClient = gmClient,
        measurementRepository = measurementRepository,
        swsUsername = backendConfig.swsUsername,
        swsPassword = backendConfig.swsPassword
    )

    routing {
        configureHealthRoutes()

        configureAuthRoutes(
            authService = authService
        )

        configureCustomerRoutes(
            customerService = customerService
        )

        configureUserRoutes(
            userService = userService
        )

        configureStationRoutes(
            stationService = stationService
        )

        configureChannelRoutes(
            channelService = channelService
        )

        configureMeasurementRoutes(
            measurementService = measurementService,
            refreshService = measurementRefreshService
        )
    }
}