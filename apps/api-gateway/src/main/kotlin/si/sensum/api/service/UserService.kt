package si.sensum.api.service

import si.sensum.api.client.BackendUserClient
import si.sensum.shared.models.users.CreateUserCommand
import si.sensum.shared.models.users.UpdateUserCommand
import si.sensum.shared.models.users.UserDto

internal class UserService(
    private val users: BackendUserClient
) {
    suspend fun getUsers(customerId: Int): List<UserDto> {
        require(customerId > 0) {
            "Customer id must be positive"
        }

        return users.getUsers(customerId)
    }

    suspend fun getUser(
        customerId: Int,
        userId: Int
    ): UserDto {
        require(customerId > 0) {
            "Customer id must be positive"
        }

        require(userId > 0) {
            "User id must be positive"
        }

        return users.getUser(
            customerId = customerId,
            userId = userId
        )
    }

    suspend fun createUser(
        customerId: Int,
        request: CreateUserCommand
    ): UserDto {
        require(customerId > 0) {
            "Customer id must be positive"
        }

        require(request.username.isNotBlank()) {
            "Username must not be blank"
        }

        require(request.password.isNotBlank()) {
            "Password must not be blank"
        }

        return users.createUser(
            customerId = customerId,
            request = request
        )
    }

    suspend fun updateUser(
        customerId: Int,
        userId: Int,
        request: UpdateUserCommand
    ): UserDto {
        require(customerId > 0) {
            "Customer id must be positive"
        }

        require(userId > 0) {
            "User id must be positive"
        }

        return users.updateUser(
            customerId = customerId,
            userId = userId,
            request = request
        )
    }

    suspend fun deleteUser(
        customerId: Int,
        userId: Int
    ) {
        require(customerId > 0) {
            "Customer id must be positive"
        }

        require(userId > 0) {
            "User id must be positive"
        }

        users.deleteUser(
            customerId = customerId,
            userId = userId
        )
    }
}