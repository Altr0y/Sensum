package si.sensum.shared.models.common

import kotlinx.serialization.Serializable

@Serializable
data class HealthDto(
    val status: String,
    val service: String
)