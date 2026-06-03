package si.sensum.shared.models.users

import kotlinx.serialization.Serializable

@Serializable
enum class UserRoleDto {
    ADMIN,
    USER
}