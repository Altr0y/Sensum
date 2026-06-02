package si.sensum.gm.auth

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import si.sensum.gm.services.AuthService
import si.sensum.shared.auth.bearer.BearerToken
import si.sensum.gm.model.GmUserSession
import si.sensum.shared.models.common.ApiError
import si.sensum.sws.model.SwsSession

internal suspend fun ApplicationCall.resolveGmTokenOrRespond(): String? {
    val token = BearerToken.extract(request.headers[HttpHeaders.Authorization])

    if (token == null) {
        respondUnauthorized("Missing bearer token")
        return null
    }

    return token
}

internal suspend fun ApplicationCall.resolveUserSessionOrRespond(
    authService: AuthService
): GmUserSession? {
    val token = resolveGmTokenOrRespond() ?: return null
    val session = authService.findSession(token)

    if (session == null) {
        respondUnauthorized("Invalid or expired session")
        return null
    }

    return session
}

internal suspend fun ApplicationCall.resolveSwsSessionOrRespond(
    authService: AuthService
): SwsSession? {
    val userSession = resolveUserSessionOrRespond(authService) ?: return null
    return userSession.toSwsSession()
}

private suspend fun ApplicationCall.respondUnauthorized(message: String) {
    respond(
        HttpStatusCode.Unauthorized,
        ApiError(error = message)
    )
}