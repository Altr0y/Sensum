package si.sensum.shared.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResult(
    val token: String,
    val expiresAt: String
)