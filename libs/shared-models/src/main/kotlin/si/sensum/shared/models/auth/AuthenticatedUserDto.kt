package si.sensum.shared.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUserDto(
    val id: Int,
    val username: String,
    val customerId: Int,
    val role: String
)