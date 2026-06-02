package si.sensum.backend.service

import si.sensum.backend.mapper.toDomain
import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.UserRepository
import si.sensum.shared.models.users.CreateUserCommand
import si.sensum.shared.models.users.UpdateUserCommand
import si.sensum.shared.models.users.UserDto

class UserService(
    private val userRepository: UserRepository
) {
    fun getUsersByCustomer(
        customerId: Int
    ): List<UserDto> {
        return userRepository
            .findByCustomerId(customerId)
            .map { it.toDto() }
    }

    fun createUser(
        customerId: Int,
        request: CreateUserCommand
    ): UserDto {
        require(request.username.isNotBlank()) {
            "Username must not be blank"
        }

        require(request.password.isNotBlank()) {
            "Password must not be blank"
        }

        return userRepository
            .create(
                customerId = customerId,
                username = request.username,
                passwordHash = request.password, // TODO: kasneje hash
                role = request.role.toDomain(),
                enabled = request.enabled
            )
            .toDto()
    }

    fun getUser(
        customerId: Int,
        userId: Int
    ): UserDto {
        return userRepository
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
    ): UserDto {
        return userRepository
            .update(
                customerId = customerId,
                userId = userId,
                username = request.username,
                passwordHash = request.password,
                role = request.role?.toDomain(),
                enabled = request.enabled
            )
            ?.toDto()
            ?: error("User not found")
    }

    fun deleteUser(
        customerId: Int,
        userId: Int
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