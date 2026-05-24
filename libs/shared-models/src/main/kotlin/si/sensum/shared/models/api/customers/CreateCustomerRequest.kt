package si.sensum.shared.models.api.customers

import kotlinx.serialization.Serializable

@Serializable
data class CreateCustomerRequest(
    val name: String
)