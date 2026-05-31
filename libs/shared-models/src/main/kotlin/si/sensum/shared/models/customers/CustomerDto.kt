package si.sensum.shared.models.customers

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    val id: Int,
    val name: String
)