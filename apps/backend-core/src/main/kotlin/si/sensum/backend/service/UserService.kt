package si.sensum.backend.service

import si.sensum.backend.mapper.toDomain
import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.UserRepository
import si.sensum.backend.security.PasswordHasher
import si.sensum.shared.models.users.CreateUserCommand
import si.sensum.shared.models.users.UpdateUserCommand
import si.sensum.shared.models.users.UserDto

class UserService(
    private val userRepository: UserRepository
) {
    fun getUsersByCustomer(
        customerId: Int
    ): List<UserDto> = ServiceLogger.call(
        service = "user",
        operation = "getUsersByCustomer",
        details = "customerId=$customerId"
    ) {
        userRepository
            .findByCustomerId(customerId)
            .map { it.toDto() }
    }

    fun createUser(
        customerId: Int,
        request: CreateUserCommand
    ): UserDto = ServiceLogger.call(
        service = "user",
        operation = "createUser",
        details = "customerId=$customerId username=${request.username.trim().lowercase()}"
    ) {
        require(customerId > 0) {
            "Customer id must be positive"
        }

        require(request.username.isNotBlank()) {
            "Username must not be blank"
        }

        require(request.password.isNotBlank()) {
            "Password must not be blank"
        }

        userRepository
            .create(
                customerId = customerId,
                username = request.username.trim().lowercase(),
                passwordHash = PasswordHasher.hash(request.password),
                role = request.role.toDomain(),
                enabled = request.enabled
            )
            .toDto()
    }

    fun getUser(
        customerId: Int,
        userId: Int
    ): UserDto = ServiceLogger.call(
        service = "user",
        operation = "getUser",
        details = "customerId=$customerId userId=$userId"
    ) {
        userRepository
            .findByCustomerAndId(
                customerId = customerId,
                userId = userId
            )
            ?.toDto()
            ?: error("User not found")
    }

    fun updateUser(
        customerId: Int,
        userId: Int,
        request: UpdateUserCommand
    ): UserDto = ServiceLogger.call(
        service = "user",
        operation = "updateUser",
        details = "customerId=$customerId userId=$userId"
    ) {
        val normalizedUsername = request.username
            ?.trim()
            ?.lowercase()
            ?.takeIf { it.isNotBlank() }

        val passwordHash = request.password
            ?.takeIf { it.isNotBlank() }
            ?.let { PasswordHasher.hash(it) }

        userRepository
            .update(
                customerId = customerId,
                userId = userId,
                username = normalizedUsername,
                passwordHash = passwordHash,
                role = request.role?.toDomain(),
                enabled = request.enabled
            )
            ?.toDto()
            ?: error("User not found")
    }

    fun deleteUser(
        customerId: Int,
        userId: Int
    ) = ServiceLogger.call(
        service = "user",
        operation = "deleteUser",
        details = "customerId=$customerId userId=$userId"
    ) {
        val deleted = userRepository.delete(
            customerId = customerId,
            userId = userId
        )

        if (!deleted) {
            error("User not found")
        }
    }
}