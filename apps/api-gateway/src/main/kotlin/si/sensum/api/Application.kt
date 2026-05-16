package si.sensum.api

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import si.sensum.api.backend.BackendClient
import si.sensum.api.config.ApiGatewayConfig
import si.sensum.api.config.createHttpClient
import si.sensum.api.routes.authRoutes
import si.sensum.api.routes.measurementRoutes
import si.sensum.shared.models.api.HealthResponse
import si.sensum.shared.auth.jwt.JwtConfig
import si.sensum.shared.auth.jwt.JwtTokenService
import java.util.UUID
import si.sensum.api.plugins.installApiErrorHandling
import si.sensum.logging.installHttpRequestLogging

object ApiInfo {
    const val NAME = "API Gateway"
}

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val appConfig = loadAppConfig()

    val jwtTokenService = JwtTokenService(
        config = JwtConfig(
            secret = appConfig.jwtSecret,
            issuer = appConfig.jwtIssuer,
            audience = appConfig.jwtAudience,
            ttlSeconds = appConfig.jwtTtlSeconds
        )
    )

    installPlugins()
    installJwtAuthentication(
        appConfig = appConfig,
        jwtTokenService = jwtTokenService
    )

    val httpClient = createHttpClient()

    val backendClient = BackendClient(
        httpClient = httpClient,
        baseUrl = appConfig.backendCoreBaseUrl
    )

    configureRoutes(
        backendClient = backendClient,
        jwtTokenService = jwtTokenService,
        appConfig = appConfig
    )
}

private fun Application.loadAppConfig(): ApiGatewayConfig {
    val config = environment.config

    val backendCoreBaseUrl = config.property("backendCore.baseUrl").getString()

    val authUsername = config.property("auth.username").getString()
    val authPassword = config.property("auth.password").getString()

    val jwtSecret = config.property("jwt.secret").getString()
    val jwtIssuer = config.property("jwt.issuer").getString()
    val jwtAudience = config.property("jwt.audience").getString()
    val jwtRealm = config.property("jwt.realm").getString()
    val jwtTtlSeconds = config.property("jwt.ttlSeconds").getString().toLong()

    require(backendCoreBaseUrl.isNotBlank()) {
        "Missing backendCore baseUrl. Set BACKEND_CORE_BASE_URL env variable."
    }

    require(authUsername.isNotBlank()) {
        "Missing auth username. Set API_AUTH_USERNAME env variable."
    }

    require(authPassword.isNotBlank()) {
        "Missing auth password. Set API_AUTH_PASSWORD env variable."
    }

    require(jwtSecret.length >= 32) {
        "JWT secret must be at least 32 characters long."
    }

    return ApiGatewayConfig(
        backendCoreBaseUrl = backendCoreBaseUrl,
        authUsername = authUsername,
        authPassword = authPassword,
        jwtSecret = jwtSecret,
        jwtIssuer = jwtIssuer,
        jwtAudience = jwtAudience,
        jwtRealm = jwtRealm,
        jwtTtlSeconds = jwtTtlSeconds
    )
}

private fun Application.installJwtAuthentication(
    appConfig: ApiGatewayConfig,
    jwtTokenService: JwtTokenService
) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = appConfig.jwtRealm

            verifier(jwtTokenService.verifier())

            validate { credential ->
                val username = credential.payload.getClaim("username").asString()

                if (!username.isNullOrBlank()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    io.ktor.http.HttpStatusCode.Unauthorized,
                    si.sensum.shared.models.api.ApiErrorResponse(
                        error = "Token is not valid or has expired"
                    )
                )
            }
        }
    }
}

private fun Application.installPlugins() {
    installApiErrorHandling()

    install(ContentNegotiation) {
        json()
    }

    install(CallId) {
        generate { UUID.randomUUID().toString() }
        verify { callId -> callId.isNotBlank() }
        replyToHeader("X-Request-Id")
    }
    installHttpRequestLogging(
        serviceName = "api-gateway"
    )
}

private fun Application.configureRoutes(
    backendClient: BackendClient,
    jwtTokenService: JwtTokenService,
    appConfig: ApiGatewayConfig
) {
    routing {
        get("/health") {
            call.respond(
                HealthResponse(
                    status = "ok",
                    service = "api-gateway"
                )
            )
        }

        authRoutes(
            authUsername = appConfig.authUsername,
            authPassword = appConfig.authPassword,
            jwtTokenService = jwtTokenService
        )

        authenticate("auth-jwt") {
            measurementRoutes(
                backendClient = backendClient
            )
        }
    }
}