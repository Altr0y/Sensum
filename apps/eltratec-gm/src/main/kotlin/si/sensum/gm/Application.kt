package si.sensum.gm

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

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

    routing {
        get("/health") {
            call.respond(HealthResponse(status = "ok", service = "eltratec-gm"))
        }
    }
}

@Serializable
data class HealthResponse(
    val status: String,
    val service: String
)