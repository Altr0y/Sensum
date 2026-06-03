package si.sensum.shared.models.customers

import kotlinx.serialization.Serializable

@Serializable
data class CreateCustomerCommand(
    val name: String
)