package si.sensum.shared.models.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class DemoLoginRequest(
    val username: String,
    val password: String
)