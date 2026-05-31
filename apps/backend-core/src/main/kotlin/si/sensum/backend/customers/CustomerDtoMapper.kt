package si.sensum.backend.customers

import si.sensum.shared.models.customers.CustomerDto

fun CustomerEntity.toDto(): CustomerDto {
    return CustomerDto(
        id = id,
        name = name
    )
}