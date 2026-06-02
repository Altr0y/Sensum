package si.sensum.shared.models.users

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserCommand(
    val username: String? = null,
    val password: String? = null,
    val role: UserRoleDto? = null,
    val enabled: Boolean? = null
)