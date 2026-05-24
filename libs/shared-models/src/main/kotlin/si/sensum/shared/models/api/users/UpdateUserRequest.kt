package si.sensum.shared.models.api.users

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val username: String? = null,
    val password: String? = null,
    val role: UserRoleDto? = null,
    val enabled: Boolean? = null
)