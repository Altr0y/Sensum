package si.sensum.api.config

import io.ktor.server.application.Application

internal fun Application.loadApiGatewayConfig(): ApiGatewayConfig {
    val config = environment.config

    val backendCoreBaseUrl = config.property("backendCore.baseUrl").getString()

    val jwtSecret = config.property("jwt.secret").getString()
    val jwtIssuer = config.property("jwt.issuer").getString()
    val jwtAudience = config.property("jwt.audience").getString()
    val jwtRealm = config.property("jwt.realm").getString()
    val jwtTtlSeconds = config.property("jwt.ttlSeconds").getString().toLong()

    require(backendCoreBaseUrl.isNotBlank()) {
        "Missing backendCore baseUrl. Set BACKEND_CORE_BASE_URL env variable."
    }

    require(jwtSecret.length >= 32) {
        "JWT secret must be at least 32 characters long."
    }

    return ApiGatewayConfig(
        backendCoreBaseUrl = backendCoreBaseUrl,
        jwtSecret = jwtSecret,
        jwtIssuer = jwtIssuer,
        jwtAudience = jwtAudience,
        jwtRealm = jwtRealm,
        jwtTtlSeconds = jwtTtlSeconds
    )
}