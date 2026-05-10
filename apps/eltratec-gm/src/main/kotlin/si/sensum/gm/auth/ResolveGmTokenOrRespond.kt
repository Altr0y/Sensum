package si.sensum.gm.auth

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import si.sensum.shared.auth.bearer.BearerToken
import si.sensum.shared.models.api.ApiErrorResponse

suspend fun ApplicationCall.resolveGmTokenOrRespond(): String? {
    val token = BearerToken.extract(request.headers[HttpHeaders.Authorization])

    if (token == null) {
        respond(
            HttpStatusCode.Unauthorized,
            ApiErrorResponse(error = "Missing bearer token")
        )
        return null
    }

    return token
}