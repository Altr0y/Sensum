package si.sensum.backend.customers

import si.sensum.shared.models.api.customers.CustomerDto

fun CustomerEntity.toDto(): CustomerDto {
    return CustomerDto(
        id = id,
        name = name
    )
}