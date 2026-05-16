package si.sensum.gm.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import si.sensum.gm.auth.resolveGmTokenOrRespond
import si.sensum.gm.errors.MissingRequiredFieldException
import si.sensum.gm.services.AuthService
import si.sensum.shared.models.api.LoginRequest

internal fun Route.gmLoginRoute(
    authService: AuthService
) {
    post("/auth/login") {
        val request = call.receiveLoginRequest()

        val response = authService.login(
            username = request.username,
            password = request.password
        )

        call.respond(HttpStatusCode.OK, response)
    }
}

internal fun Route.gmLogoutRoute(
    authService: AuthService
) {
    post("/auth/logout") {
        val token = call.resolveGmTokenOrRespond() ?: return@post

        authService.logout(token)

        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "status" to "ok",
                "message" to "Logged out"
            )
        )
    }
}

private suspend fun ApplicationCall.receiveLoginRequest(): LoginRequest {
    val request = receive<LoginRequest>()

    if (request.username.isBlank()) {
        throw MissingRequiredFieldException("username")
    }

    if (request.password.isBlank()) {
        throw MissingRequiredFieldException("password")
    }

    return request
}