package si.sensum.api.auth

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import si.sensum.shared.auth.bearer.BearerToken
import si.sensum.shared.models.api.ApiErrorResponse

suspend fun ApplicationCall.requireApiBearerToken(
    validator: ApiTokenValidator
): Boolean {
    val token = BearerToken.extract(request.headers[HttpHeaders.Authorization])

    if (token == null || !validator.isValid(token)) {
        respond(
            HttpStatusCode.Unauthorized,
            ApiErrorResponse("Invalid or missing bearer token")
        )
        return false
    }

    return true
}