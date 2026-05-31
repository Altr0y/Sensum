package si.sensum.backend.service

import si.sensum.backend.mapper.toDto
import si.sensum.backend.repository.CustomerRepository
import si.sensum.shared.models.customers.CreateCustomerCommand
import si.sensum.shared.models.customers.CustomerDto
import si.sensum.shared.models.customers.UpdateCustomerCommand

class CustomerService(
    private val customerRepository: CustomerRepository
) {
    fun createCustomer(
        request: CreateCustomerCommand
    ): CustomerDto {
        require(request.name.isNotBlank()) {
            "Customer name must not be blank"
        }

        return customerRepository
            .create(name = request.name)
            .toDto()
    }

    fun getCustomer(
        customerId: Int
    ): CustomerDto {
        return customerRepository
            .findById(customerId)
            ?.toDto()
            ?: error("Customer not found")
    }

    fun updateCustomer(
        customerId: Int,
        request: UpdateCustomerCommand
    ): CustomerDto {
        require(request.name.isNotBlank()) {
            "Customer name must not be blank"
        }

        return customerRepository
            .update(
                customerId = customerId,
                name = request.name
            )
            ?.toDto()
            ?: error("Customer not found")
    }
}