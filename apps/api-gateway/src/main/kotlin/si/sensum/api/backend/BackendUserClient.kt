package si.sensum.api.backend

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.users.CreateUserCommand
import si.sensum.shared.models.users.UpdateUserCommand
import si.sensum.shared.models.users.UserDto

internal class BackendUserClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getUsers(customerId: Int): List<UserDto> {
        return backend.get("/api/v1/customers/$customerId/users")
    }

    suspend fun getUser(
        customerId: Int,
        userId: Int
    ): UserDto {
        return backend.get("/api/v1/customers/$customerId/users/$userId")
    }

    suspend fun createUser(
        customerId: Int,
        request: CreateUserCommand
    ): UserDto {
        return backend.post("/api/v1/customers/$customerId/users", request)
    }

    suspend fun updateUser(
        customerId: Int,
        userId: Int,
        request: UpdateUserCommand
    ): UserDto {
        return backend.put("/api/v1/customers/$customerId/users/$userId", request)
    }

    suspend fun deleteUser(
        customerId: Int,
        userId: Int
    ) {
        backend.delete("/api/v1/customers/$customerId/users/$userId")
    }
}