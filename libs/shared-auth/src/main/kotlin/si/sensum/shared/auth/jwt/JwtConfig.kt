package si.sensum.shared.auth.jwt

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val ttlSeconds: Long
)