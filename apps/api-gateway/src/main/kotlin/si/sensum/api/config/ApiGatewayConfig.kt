package si.sensum.api.config

internal data class ApiGatewayConfig(
    val backendCoreBaseUrl: String,
    val jwtPrivateKeyBase64: String,
    val jwtIssuer: String,
    val jwtAudience: String,
    val jwtRealm: String,
    val jwtTtlSeconds: Long
)