package si.sensum.api.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.LoginRequest
import si.sensum.shared.models.api.LoginResponse
import java.time.Instant

fun Route.authRoutes(
    demoUsername: String,
    demoPassword: String,
    demoAuthToken: String
) {
    route("/api/v1/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()

            if (request.username != demoUsername || request.password != demoPassword) {
                call.respond(HttpStatusCode.Unauthorized, ApiErrorResponse("Invalid demo login"))
                return@post
            }

            call.respond(
                LoginResponse(
                    token = demoAuthToken,
                    expiresAt = Instant.now().plusSeconds(8 * 60 * 60).toString()
                )
            )
        }
    }
}