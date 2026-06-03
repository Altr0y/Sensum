package si.sensum.backend.service

import si.sensum.backend.repository.UserRepository
import si.sensum.shared.models.auth.AuthenticatedUserDto

class AuthService(
    private val userRepository: UserRepository
) {
    fun verifyLogin(
        username: String,
        password: String
    ): AuthenticatedUserDto {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("Invalid username or password")

        if (!user.enabled) {
            throw IllegalArgumentException("User is disabled")
        }

        if (!passwordMatches(password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid username or password")
        }

        return AuthenticatedUserDto(
            id = user.id,
            username = user.username,
            customerId = user.customerId,
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