package si.sensum.api.auth

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import si.sensum.shared.auth.bearer.BearerToken
import si.sensum.shared.auth.bearer.TokenValidator
import si.sensum.shared.models.api.ApiErrorResponse

suspend fun ApplicationCall.requireApiBearerToken(
    validator: TokenValidator
): Boolean {
    val token = BearerToken.extract(request.headers[HttpHeaders.Authorization])

    if (token == null || !validator.isValid(token)) {
        respond(
            HttpStatusCode.Unauthorized,
            ApiErrorResponse(error = "Invalid or missing bearer token")
        )
        return false
    }

    return true
}