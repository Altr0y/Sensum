package si.sensum.backend.service

import si.sensum.backend.repository.UserRepository
import si.sensum.backend.security.PasswordHasher
import si.sensum.logging.Logger
import si.sensum.shared.models.auth.AuthenticatedUserDto

class AuthService(
    private val userRepository: UserRepository
) {
    fun verifyLogin(
        username: String,
        password: String
    ): AuthenticatedUserDto {
        val normalizedUsername = username.trim().lowercase()

        if (normalizedUsername.isBlank()) {
            Logger.log.warn {
                "[AUTH] Login failed: username is blank"
            }
            throw IllegalArgumentException("Username is required")
        }

        if (password.isBlank()) {
            Logger.log.warn {
                "[AUTH] Login failed: password is blank username=$normalizedUsername"
            }
            throw IllegalArgumentException("Password is required")
        }

        val user = userRepository.findByUsername(normalizedUsername)

        if (user == null) {
            Logger.log.warn {
                "[AUTH] Login failed: user not found username=$normalizedUsername"
            }
            throw IllegalArgumentException("Invalid username or password")
        }

        Logger.log.info {
            "[AUTH] Login attempt username=$normalizedUsername userId=${user.id} role=${user.role} enabled=${user.enabled}"
        }

        if (!user.enabled) {
            Logger.log.warn {
                "[AUTH] Login failed: user disabled username=$normalizedUsername userId=${user.id}"
            }
            throw IllegalArgumentException("User is disabled")
        }

        if (!PasswordHasher.isBcryptHash(user.passwordHash)) {
            Logger.log.error {
                "[AUTH] Login failed: stored password is not bcrypt username=$normalizedUsername userId=${user.id}"
            }
            throw IllegalArgumentException("Password is not stored securely. Update user password hash in database.")
        }

        val verified = PasswordHasher.verify(
            rawPassword = password,
            storedPasswordHash = user.passwordHash
        )

        if (!verified) {
            Logger.log.warn {
                "[AUTH] Login failed: invalid password username=$normalizedUsername userId=${user.id}"
            }
            throw IllegalArgumentException("Invalid username or password")
        }

        Logger.log.info {
            "[AUTH] Login success username=$normalizedUsername userId=${user.id} role=${user.role}"
        }

        return AuthenticatedUserDto(
            id = user.id,
            username = user.username,
            customerId = user.customerId,
            role = user.role.name
        )
    }
}