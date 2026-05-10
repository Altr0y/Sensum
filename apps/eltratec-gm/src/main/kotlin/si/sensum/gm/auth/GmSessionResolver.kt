package si.sensum.gm.auth

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import si.sensum.gm.services.AuthService
import si.sensum.shared.auth.bearer.BearerToken
import si.sensum.shared.auth.model.UserSession
import si.sensum.shared.models.api.ApiErrorResponse

suspend fun ApplicationCall.resolveUserSessionOrRespond(
    authService: AuthService
): UserSession? {
    val token = BearerToken.extract(request.headers[HttpHeaders.Authorization])

    if (token == null) {
        respondUnauthorized("Missing bearer token")
        return null
    }

    val session = authService.findSession(token)

    if (session == null) {
        respondUnauthorized("Invalid or expired session")
        return null
    }

    return session
}

private suspend fun ApplicationCall.respondUnauthorized(message: String) {
    respond(
        HttpStatusCode.Unauthorized,
        ApiErrorResponse(error = message)
    )
}