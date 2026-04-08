package si.sensum.shared.models.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val error: String
)