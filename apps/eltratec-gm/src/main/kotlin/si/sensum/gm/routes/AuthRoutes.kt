package si.sensum.gm.routes

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.gm.services.AuthService
import si.sensum.shared.models.api.LoginRequest

fun Route.authRoutes(authService: AuthService) {
    route("/api/v1") {
        post("/auth/login") {
            val request = call.receive<LoginRequest>()

            val response = authService.login(
                username = request.username,
                password = request.password
            )

            call.respond(response)
        }
    }
}