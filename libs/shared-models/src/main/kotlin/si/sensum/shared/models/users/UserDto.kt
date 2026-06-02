package si.sensum.shared.models.users

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val customerId: Int,
    val username: String,
    val role: UserRoleDto,
    val enabled: Boolean
)