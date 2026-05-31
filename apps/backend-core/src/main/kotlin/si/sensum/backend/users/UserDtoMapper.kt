package si.sensum.backend.users

import si.sensum.shared.models.users.UserDto
import si.sensum.shared.models.users.UserRoleDto

fun UserEntity.toDto(): UserDto {
    return UserDto(
        id = id,
        customerId = customerId,
        username = username,
        role = role.toDto(),
        enabled = enabled
    )
}

fun UserRole.toDto(): UserRoleDto {
    return when (this) {
        UserRole.ADMIN -> UserRoleDto.ADMIN
        UserRole.USER -> UserRoleDto.USER
    }
}

fun UserRoleDto.toDomain(): UserRole {
    return when (this) {
        UserRoleDto.ADMIN -> UserRole.ADMIN
        UserRoleDto.USER -> UserRole.USER
    }
}