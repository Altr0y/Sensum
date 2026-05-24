package si.sensum.shared.models.api.customers

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    val id: Int,
    val name: String
)