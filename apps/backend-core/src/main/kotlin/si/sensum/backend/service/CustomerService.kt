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
    ): CustomerDto = ServiceLogger.call(
        service = "customer",
        operation = "createCustomer",
        details = "name=${request.name.trim()}"
    ) {
        require(request.name.isNotBlank()) {
            "Customer name must not be blank"
        }

        customerRepository
            .create(name = request.name.trim())
            .toDto()
    }

    fun getCustomer(
        customerId: Int
    ): CustomerDto = ServiceLogger.call(
        service = "customer",
        operation = "getCustomer",
        details = "customerId=$customerId"
    ) {
        customerRepository
            .findById(customerId)
            ?.toDto()
            ?: error("Customer not found")
    }

    fun updateCustomer(
        customerId: Int,
        request: UpdateCustomerCommand
    ): CustomerDto = ServiceLogger.call(
        service = "customer",
        operation = "updateCustomer",
        details = "customerId=$customerId"
    ) {
        require(request.name.isNotBlank()) {
            "Customer name must not be blank"
        }

        customerRepository
            .update(
                customerId = customerId,
                name = request.name.trim()
            )
            ?.toDto()
            ?: error("Customer not found")
    }
}