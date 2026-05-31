package si.sensum.api.routes

import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.api.auth.requireAuthenticatedUser
import si.sensum.api.services.AuthService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.auth.LoginCommand
import si.sensum.shared.models.auth.AuthenticatedUserDto

internal fun Route.publicAuthRoutes(
    authService: AuthService
) {
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginCommand>()

            call.respondOk {
                authService.login(request)
            }
        }
    }
}

internal fun Route.protectedAuthRoutes(
    authService: AuthService
) {
    route("/auth") {
        get("/me") {
            val principal = call.requireAuthenticatedUser() ?: return@get

            call.respondOk {
                AuthenticatedUserDto(
                    id = principal.userId,
                    username = principal.username,
                    customerId = principal.customerId,
                    role = principal.role
                )
            }
        }

        post("/logout") {
            call.respondOk {
                authService.logout()
            }
        }
    }
}