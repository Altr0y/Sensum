package si.sensum.shared.models.users

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserCommand(
    val username: String,
    val password: String,
    val role: UserRoleDto = UserRoleDto.USER,
    val enabled: Boolean = true
)