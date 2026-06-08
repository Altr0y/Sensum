package si.sensum.api.config

import io.ktor.server.application.Application

internal fun Application.loadApiGatewayConfig(): ApiGatewayConfig {
    val config = environment.config

    val backendCoreBaseUrl = config.property("backendCore.baseUrl").getString()

    val jwtPrivateKeyBase64 = config.propertyOrNull("jwt.privateKey")?.getString() ?: ""
    val jwtIssuer = config.property("jwt.issuer").getString()
    val jwtAudience = config.property("jwt.audience").getString()
    val jwtRealm = config.property("jwt.realm").getString()
    val jwtTtlSeconds = config.property("jwt.ttlSeconds").getString().toLong()

    require(backendCoreBaseUrl.isNotBlank()) {
        "Missing backendCore baseUrl. Set BACKEND_CORE_BASE_URL env variable."
    }

    return ApiGatewayConfig(
        backendCoreBaseUrl = backendCoreBaseUrl,
        jwtPrivateKeyBase64 = jwtPrivateKeyBase64,
        jwtIssuer = jwtIssuer,
        jwtAudience = jwtAudience,
        jwtRealm = jwtRealm,
        jwtTtlSeconds = jwtTtlSeconds
    )
}