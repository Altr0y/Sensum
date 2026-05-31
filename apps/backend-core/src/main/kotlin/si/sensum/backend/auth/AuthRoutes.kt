package si.sensum.backend.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.shared.models.auth.LoginCommand

fun Route.authRoutes(
    authService: AuthService
) {
    route("/api/v1/auth") {
        post("/verify") {
            val request = call.receive<LoginCommand>()

            val authenticatedUser = authService.verifyLogin(
                username = request.username,
                password = request.password
            )

            call.respond(HttpStatusCode.OK, authenticatedUser)
        }
    }
}