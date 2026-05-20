package si.sensum.backend.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.shared.models.api.LoginRequest

fun Route.authRoutes(
    authService: AuthService
) {
    route("/api/v1/auth") {
        post("/verify") {
            val request = call.receive<LoginRequest>()

            val authenticatedUser = authService.verifyLogin(
                username = request.username,
                password = request.password
            )

            call.respond(HttpStatusCode.OK, authenticatedUser)
        }
    }
}