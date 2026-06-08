package si.sensum.api.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.api.domain.requireAuthenticatedUser
import si.sensum.api.service.AuthService
import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.auth.LoginCommand
import si.sensum.shared.models.auth.AuthenticatedUserDto

internal fun Route.publicAuthController(
    authService: AuthService,
    jwtTokenService: JwtTokenService
) {
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginCommand>()

            call.respondOk {
                authService.login(request)
            }
        }

        get("/.well-known/jwks.json") {
            val jwks = jwtTokenService.jwks()
            if (jwks != null) {
                call.respond(jwks)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

internal fun Route.protectedAuthController(
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