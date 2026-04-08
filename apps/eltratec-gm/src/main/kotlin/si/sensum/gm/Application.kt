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
import si.sensum.gm.config.createHttpClient
import si.sensum.gm.routes.authRoutes
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

fun Application.module() {
    val log = Logger.log
    val config = environment.config

    val swsBaseUrl = config.property("sws.baseUrl").getString()
    val swsTimeout = config.property("sws.timeoutMillis").getString().toLong()
    val tokenTtlHours = config.property("gm.auth.tokenTtlHours").getString().toLong()

    require(swsBaseUrl.isNotBlank()) {
        "Missing SWS baseUrl (set SWS_BASE_URL env variable)"
    }

    install(ContentNegotiation) {
        json()
    }

    install(CallId) {
        generate {
            UUID.randomUUID().toString()
        }

        verify { callId ->
            callId.isNotBlank()
        }

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

        log.info {
            "[HTTP] $method $normalizedPath status=$status duration=${duration}ms"
        }
    }

    val sessionStore = InMemorySessionStore()
    val tokenService = TokenService(Duration.ofHours(tokenTtlHours))

    val swsConfig = SwsConfig(
        baseUrl = swsBaseUrl,
        timeoutMillis = swsTimeout
    )

    val httpClient = createHttpClient(swsConfig.timeoutMillis)

    val soapClient = SmartWebSoapClient(
        httpClient = httpClient,
        baseUrl = swsConfig.baseUrl
    )

    val authService = AuthService(
        soapClient = soapClient,
        tokenService = tokenService,
        sessionStore = sessionStore
    )

    routing {
        get("/health") {
            call.respond(
                HealthResponse(
                    status = "ok",
                    service = "eltratec-gm"
                )
            )
        }

        authRoutes(authService)
    }
}