package si.sensum.backend.users

data class UserEntity(
    val id: Int,
    val customerId: Int,
    val username: String,
    val passwordHash: String,
    val role: UserRole
)