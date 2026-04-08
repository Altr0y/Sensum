package si.sensum.gm

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.gm.routes.authRoutes
import si.sensum.gm.services.AuthService
import si.sensum.gm.config.createHttpClient
import si.sensum.shared.auth.service.TokenService
import si.sensum.shared.auth.store.InMemorySessionStore
import si.sensum.shared.models.api.HealthResponse
import si.sensum.sws.SmartWebSoapClient
import si.sensum.sws.SwsConfig
import java.time.Duration

object ApiInfo {
    const val NAME = "Eltratec GM"
}

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
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