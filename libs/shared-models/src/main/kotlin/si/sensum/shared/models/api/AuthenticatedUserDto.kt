package si.sensum.shared.models.api

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUserDto(
    val userId: Int,
    val username: String,
    val customerId: Int,
    val role: String
)