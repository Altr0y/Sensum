package si.sensum.api.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.auth.jwt.JwtUser
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.LoginRequest
import si.sensum.shared.models.api.LoginResponse

fun Route.authRoutes(
    authUsername: String,
    authPassword: String,
    jwtTokenService: JwtTokenService
) {
    route("/api/v1/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()

            if (request.username != authUsername || request.password != authPassword) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiErrorResponse(error = "Invalid username or password")
                )
                return@post
            }

            val user = JwtUser(
                username = request.username,
                role = "USER"
            )

            val token = jwtTokenService.generateToken(user)
            val expiresAt = jwtTokenService.expiresAt().toString()

            call.respond(
                LoginResponse(
                    token = token,
                    expiresAt = expiresAt
                )
            )
        }
    }
}