package si.sensum.gm.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.gm.auth.requireGmBearerToken
import si.sensum.gm.services.AuthService
import si.sensum.shared.auth.bearer.TokenValidator
import si.sensum.shared.models.api.LoginRequest

fun Route.authRoutes(
    authService: AuthService,
    tokenValidator: TokenValidator
) {
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()

            val response = authService.login(
                username = request.username,
                password = request.password
            )

            call.respond(HttpStatusCode.OK, response)
        }
    }

    get("/protected") {
        if (!call.requireGmBearerToken(tokenValidator)) return@get

        call.respond(
            mapOf(
                "status" to "ok",
                "message" to "Authorized GM request"
            )
        )
    }
}