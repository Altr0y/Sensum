package si.sensum.shared.auth.jwt

data class JwtConfig(
    val issuer: String,
    val audience: String,
    val ttlSeconds: Long,
    val secret: String = "",
    val privateKeyBase64: String? = null
)