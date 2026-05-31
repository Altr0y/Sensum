package si.sensum.backend.domain.user

data class UserEntity(
    val id: Int,
    val customerId: Int,
    val username: String,
    val passwordHash: String,
    val role: UserRole,
    val enabled: Boolean
)