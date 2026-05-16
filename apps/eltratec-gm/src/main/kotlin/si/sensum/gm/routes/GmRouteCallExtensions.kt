package si.sensum.gm.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import si.sensum.gm.auth.resolveSwsSessionOrRespond
import si.sensum.gm.services.AuthService
import si.sensum.sws.model.SwsSession

internal suspend fun ApplicationCall.gmRouteCall(
    authService: AuthService,
    block: suspend (SwsSession) -> Any
) {
    val session = resolveSwsSessionOrRespond(authService) ?: return

    val response = block(session)

    respond(HttpStatusCode.OK, response)
}