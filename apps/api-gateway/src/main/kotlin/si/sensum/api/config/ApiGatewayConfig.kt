package si.sensum.api.config

data class ApiGatewayConfig(
    val backendCoreBaseUrl: String,
    val authUsername: String,
    val authPassword: String,
    val jwtSecret: String,
    val jwtIssuer: String,
    val jwtAudience: String,
    val jwtRealm: String,
    val jwtTtlSeconds: Long
)