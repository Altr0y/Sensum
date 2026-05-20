package si.sensum.backend.auth

import si.sensum.backend.users.UserRepository
import si.sensum.shared.models.api.AuthenticatedUserResponse

class AuthService(
    private val userRepository: UserRepository
) {
    fun verifyLogin(
        username: String,
        password: String
    ): AuthenticatedUserResponse {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("Invalid username or password")

        if (!passwordMatches(password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid username or password")
        }

        return AuthenticatedUserResponse(
            id = user.id,
            username = user.username,
            role = user.role.name
        )
    }

    private fun passwordMatches(
        rawPassword: String,
        storedPasswordHash: String
    ): Boolean {
        // Za zdaj podpira oboje:
        // 1. dev način: password_hash = "sensum"
        // 2. kasneje pravi hash
        return rawPassword == storedPasswordHash
    }
}