package si.sensum.backend.users

data class User(
    val id: Int,
    val customerId: Int = 0, //TO DO: fix userRepository, remove default
    val username: String,
    val passwordHash: String,
    val role: UserRole
)