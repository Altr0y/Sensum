package si.sensum.gm

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.gm.routes.authRoutes
import si.sensum.gm.services.AuthService
import si.sensum.shared.auth.service.TokenService
import si.sensum.shared.auth.store.InMemorySessionStore
import si.sensum.shared.models.api.HealthResponse
import si.sensum.sws.SmartWebSoapClientMock
import java.time.Duration

object ApiInfo {
    const val NAME = "Eltratec GM"
}


fun main() {
    embeddedServer(
        factory = Netty,
        port = 8081,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    val sessionStore = InMemorySessionStore()
    val tokenService = TokenService(Duration.ofHours(8))
    val soapClient = SmartWebSoapClientMock()
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