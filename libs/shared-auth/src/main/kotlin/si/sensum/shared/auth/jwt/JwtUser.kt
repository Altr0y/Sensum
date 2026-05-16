package si.sensum.shared.auth.jwt

data class JwtUser(
    val username: String,
    val role: String = "USER",
    val serviceName: String? = null
)