package si.sensum.api.domain

import io.ktor.server.auth.jwt.JWTPrincipal

internal data class ApiPrincipal(
    val userId: Int,
    val username: String,
    val customerId: Int,
    val role: String
)

internal fun JWTPrincipal.toApiPrincipal(): ApiPrincipal {
    val userId = payload.getClaim("userId").asInt()
        ?: error("JWT is missing userId")

    val username = payload.getClaim("username").asString()
        ?: error("JWT is missing username")

    val customerId = payload.getClaim("customerId").asInt()
        ?: error("JWT is missing customerId")

    val role = payload.getClaim("role").asString()
        ?: error("JWT is missing role")

    return ApiPrincipal(
        userId = userId,
        username = username,
        customerId = customerId,
        role = role
    )
}