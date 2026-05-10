package si.sensum.gm.auth

import io.ktor.server.application.*
import si.sensum.gm.services.AuthService
import si.sensum.sws.model.SwsSession

suspend fun ApplicationCall.resolveSwsSessionOrRespond(
    authService: AuthService
): SwsSession? {
    val userSession = resolveUserSessionOrRespond(authService) ?: return null

    return SwsSession(
        cookieName = userSession.swsCookieName,
        cookieValue = userSession.swsCookieValue
    )
}