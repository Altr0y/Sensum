package si.sensum.shared.models.api.users

import kotlinx.serialization.Serializable

@Serializable
enum class UserRoleDto {
    ADMIN,
    USER
}