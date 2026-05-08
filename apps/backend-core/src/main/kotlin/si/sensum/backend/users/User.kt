package si.sensum.backend.users

data class User(
    val id: Int,
    val username: String,
    val passwordHash: String,
    val role: UserRole
)