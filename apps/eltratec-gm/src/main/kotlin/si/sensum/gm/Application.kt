package si.sensum.gm

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level
import si.sensum.gm.auth.StaticGmTokenValidator
import si.sensum.gm.config.createHttpClient
import si.sensum.gm.routes.authRoutes
import si.sensum.gm.routes.measurementRoutes
import si.sensum.gm.services.AuthService
import si.sensum.logging.Logger
import si.sensum.shared.auth.service.TokenService
import si.sensum.shared.auth.store.InMemorySessionStore
import si.sensum.shared.models.api.HealthResponse
import si.sensum.sws.SmartWebSoapClient
import si.sensum.sws.SwsConfig
import java.time.Duration
import java.util.UUID

object ApiInfo {
    const val NAME = "Eltratec GM"
}

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val appConfig = loadAppConfig()

    installPlugins()

    val sessionStore = InMemorySessionStore()
    val tokenService = TokenService(Duration.ofHours(appConfig.tokenTtlHours))
    val gmTokenValidator = StaticGmTokenValidator(appConfig.gmApiToken)

    val httpClient = createHttpClient(appConfig.sws.timeoutMillis)

    val soapClient = SmartWebSoapClient(
        httpClient = httpClient,
        baseUrl = appConfig.sws.baseUrl
    )

    val authService = AuthService(
        soapClient = soapClient,
        tokenService = tokenService,
        sessionStore = sessionStore
    )

    configureRoutes(
        authService = authService,
        soapClient = soapClient,
        gmTokenValidator = gmTokenValidator
    )
}

private fun Application.loadAppConfig(): GmAppConfig {
    val config = environment.config

    val swsBaseUrl = config.property("sws.baseUrl").getString()
    val swsTimeout = config.property("sws.timeoutMillis").getString().toLong()
    val tokenTtlHours = config.property("gm.auth.tokenTtlHours").getString().toLong()
    val gmApiToken = config.property("gm.auth.apiToken").getString()

    require(swsBaseUrl.isNotBlank()) {
        "Missing SWS baseUrl (set SWS_BASE_URL env variable)"
    }

    require(gmApiToken.isNotBlank()) {
        "Missing GM API token (set GM_API_AUTH_TOKEN env variable)"
    }

    return GmAppConfig(
        sws = SwsConfig(
            baseUrl = swsBaseUrl,
            timeoutMillis = swsTimeout
        ),
        tokenTtlHours = tokenTtlHours,
        gmApiToken = gmApiToken
    )
}

private fun Application.installPlugins() {
    install(ContentNegotiation) {
        json()
    }

    install(CallId) {
        generate { UUID.randomUUID().toString() }
        verify { callId -> callId.isNotBlank() }
        replyToHeader("X-Request-Id")
    }

    install(CallLogging) {
        level = Level.INFO

        filter { call ->
            call.request.path().startsWith("/api/") || call.request.path() == "/health"
        }

        format { call ->
            val method = call.request.httpMethod.value
            val normalizedPath = "/" + call.request.path().trimStart('/')
            "$method $normalizedPath"
        }

        callIdMdc("requestId")
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        val start = System.currentTimeMillis()

        proceed()

        val duration = System.currentTimeMillis() - start
        val status = call.response.status()?.value ?: 0
        val method = call.request.httpMethod.value
        val normalizedPath = "/" + call.request.path().trimStart('/')

        Logger.log.info {
            "[HTTP] $method $normalizedPath status=$status duration=${duration}ms"
        }
    }
}

private fun Application.configureRoutes(
    authService: AuthService,
    soapClient: SmartWebSoapClient,
    gmTokenValidator: StaticGmTokenValidator
) {
    routing {
        get("/health") {
            call.respond(
                HealthResponse(
                    status = "ok",
                    service = "eltratec-gm"
                )
            )
        }

        authRoutes(
            authService = authService,
            tokenValidator = gmTokenValidator
        )

        measurementRoutes(
            authService = authService,
            soapClient = soapClient
        )
    }
}

private data class GmAppConfig(
    val sws: SwsConfig,
    val tokenTtlHours: Long,
    val gmApiToken: String
)