package si.sensum.api.service

import si.sensum.api.client.BackendCustomerClient
import si.sensum.shared.models.customers.CreateCustomerCommand
import si.sensum.shared.models.customers.CustomerDto
import si.sensum.shared.models.customers.UpdateCustomerCommand

internal class CustomerService(
    private val customers: BackendCustomerClient
) {
    suspend fun getCustomer(customerId: Int): CustomerDto {
        return customers.getCustomer(customerId)
    }

    suspend fun createCustomer(
        request: CreateCustomerCommand
    ): CustomerDto {
        require(request.name.isNotBlank()) {
            "Customer name must not be blank"
        }

        return customers.createCustomer(request)
    }

    suspend fun updateCustomer(
        customerId: Int,
        request: UpdateCustomerCommand
    ): CustomerDto {
        require(customerId > 0) {
            "Customer id must be positive"
        }

        require(request.name.isNotBlank()) {
            "Customer name must not be blank"
        }

        return customers.updateCustomer(
            customerId = customerId,
            request = request
        )
    }
}