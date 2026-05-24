package si.sensum.backend

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.backend.auth.AuthService
import si.sensum.backend.auth.authRoutes
import si.sensum.backend.config.BackendConfig
import si.sensum.backend.config.createHttpClient
import si.sensum.backend.database.configureDatabases
import si.sensum.backend.gm.GmClient
import si.sensum.backend.measurements.MeasurementRefreshService
import si.sensum.backend.measurements.MeasurementRepository
import si.sensum.backend.measurements.measurementRoutes
import si.sensum.backend.plugins.installBackendErrorHandling
import si.sensum.backend.users.UserRepository
import si.sensum.backend.users.userRoutes
import si.sensum.logging.installHttpRequestLogging
import si.sensum.shared.auth.jwt.JwtConfig
import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.api.HealthResponse
import si.sensum.backend.customers.CustomerRepository
import si.sensum.backend.customers.customerRoutes
import java.util.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val backendConfig = loadBackendConfig()

    configureDatabases()
    installPlugins()
    configureRoutes(backendConfig)
}

private fun Application.loadBackendConfig(): BackendConfig {
    val config = environment.config

    return BackendConfig(
        gmBaseUrl = config.property("gm.baseUrl").getString(),
        gmServiceJwtSecret = config.property("gm.serviceJwt.secret").getString(),
        gmServiceJwtIssuer = config.property("gm.serviceJwt.issuer").getString(),
        gmServiceJwtAudience = config.property("gm.serviceJwt.audience").getString(),
        gmServiceJwtTtlSeconds = config.property("gm.serviceJwt.ttlSeconds").getString().toLong(),
        swsUsername = config.propertyOrNull("sws.username")?.getString().orEmpty(),
        swsPassword = config.propertyOrNull("sws.password")?.getString().orEmpty()
    )
}

private fun Application.installPlugins() {
    installBackendErrorHandling()

    install(ContentNegotiation) {
        json()
    }

    install(CallId) {
        generate { UUID.randomUUID().toString() }
        verify { callId -> callId.isNotBlank() }
        replyToHeader("X-Request-Id")
    }

    installHttpRequestLogging(
        serviceName = "backend-core"
    )
}

private fun Application.configureRoutes(
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

    val authService = AuthService(
        userRepository = userRepository
    )

    val measurementRepository = MeasurementRepository()

    val measurementRefreshService = MeasurementRefreshService(
        gmClient = gmClient,
        measurementRepository = measurementRepository,
        swsUsername = backendConfig.swsUsername,
        swsPassword = backendConfig.swsPassword
    )

    routing {
        get("/health") {
            call.respond(
                HealthResponse(
                    status = "ok",
                    service = "backend-core"
                )
            )
        }

        authRoutes(
            authService = authService
        )

        customerRoutes(
            customerRepository = customerRepository
        )

        userRoutes(
            userRepository = userRepository
        )

        measurementRoutes(
            measurementRepository = measurementRepository,
            refreshService = measurementRefreshService
        )
    }
}