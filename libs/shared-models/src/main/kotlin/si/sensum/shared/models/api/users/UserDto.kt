package si.sensum.shared.models.api.users

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val customerId: Int,
    val username: String,
    val role: UserRoleDto,
    val enabled: Boolean
)