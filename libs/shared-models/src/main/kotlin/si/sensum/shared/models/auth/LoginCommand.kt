package si.sensum.shared.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginCommand(
    val username: String,
    val password: String
)