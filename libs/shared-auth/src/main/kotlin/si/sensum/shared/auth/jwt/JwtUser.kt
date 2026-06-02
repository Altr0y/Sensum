package si.sensum.shared.auth.jwt

data class JwtUser(
    val username: String,
    val role: String = "USER",
    val userId: Int? = null,
    val customerId: Int? = null,
    val serviceName: String? = null
)