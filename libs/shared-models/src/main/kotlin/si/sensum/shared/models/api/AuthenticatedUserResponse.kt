package si.sensum.shared.models.api

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUserResponse(
    val id: Int,
    val username: String,
    val role: String
)