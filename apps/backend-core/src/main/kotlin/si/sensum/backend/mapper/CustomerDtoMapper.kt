package si.sensum.backend.mapper

import si.sensum.backend.domain.customer.CustomerEntity
import si.sensum.shared.models.customers.CustomerDto

fun CustomerEntity.toDto(): CustomerDto {
    return CustomerDto(
        id = id,
        name = name
    )
}