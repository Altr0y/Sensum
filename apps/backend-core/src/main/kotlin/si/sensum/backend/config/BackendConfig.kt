package si.sensum.backend.config

data class BackendConfig(
    val gmBaseUrl: String,
    val gmServiceJwtSecret: String,
    val gmServiceJwtIssuer: String,
    val gmServiceJwtAudience: String,
    val gmServiceJwtTtlSeconds: Long,
    val swsUsername: String,
    val swsPassword: String
)