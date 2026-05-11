package si.sensum.shared.models.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class DemoLoginResponse(
    val token: String,
    val username: String,
    val expiresAt: String
)