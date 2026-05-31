package si.sensum.api.domain

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.respond
import si.sensum.shared.models.common.ApiError


internal suspend fun ApplicationCall.requireAuthenticatedUser(): ApiPrincipal? {
    val jwtPrincipal = principal<JWTPrincipal>()

    if (jwtPrincipal == null) {
        respond(
            HttpStatusCode.Unauthorized,
            ApiError(error = "Missing or invalid user token")
        )
        return null
    }

    return runCatching {
        jwtPrincipal.toApiPrincipal()
    }.getOrElse {
        respond(
            HttpStatusCode.Unauthorized,
            ApiError(error = "Invalid user token claims")
        )
        null
    }
}

internal suspend fun ApplicationCall.requireAdminUser(): ApiPrincipal? {
    val principal = requireAuthenticatedUser() ?: return null

    if (principal.role != "ADMIN") {
        respond(
            HttpStatusCode.Forbidden,
            ApiError(error = "Admin role is required")
        )
        return null
    }

    return principal
}