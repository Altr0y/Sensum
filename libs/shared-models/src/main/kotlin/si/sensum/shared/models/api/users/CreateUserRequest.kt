package si.sensum.shared.models.api.users

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String,
    val role: UserRoleDto = UserRoleDto.USER,
    val enabled: Boolean = true
)