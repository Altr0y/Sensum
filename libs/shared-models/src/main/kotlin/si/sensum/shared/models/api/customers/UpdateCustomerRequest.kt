package si.sensum.shared.models.api.customers

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCustomerRequest(
    val name: String
)