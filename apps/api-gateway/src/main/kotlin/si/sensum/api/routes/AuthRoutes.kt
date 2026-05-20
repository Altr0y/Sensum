package si.sensum.api.routes

import io.ktor.server.request.*
import io.ktor.server.routing.*
import si.sensum.api.services.AuthService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.api.LoginRequest

internal fun Route.publicAuthRoutes(
    authService: AuthService
) {
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()

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
        post("/logout") {
            call.respondOk {
                authService.logout()
            }
        }
    }
}