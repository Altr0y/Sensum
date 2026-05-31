package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.customers.CreateCustomerCommand
import si.sensum.shared.models.customers.CustomerDto
import si.sensum.shared.models.customers.UpdateCustomerCommand

internal class BackendCustomerClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getCustomer(customerId: Int): CustomerDto {
        return backend.get("/api/v1/customers/$customerId")
    }

    suspend fun createCustomer(
        request: CreateCustomerCommand
    ): CustomerDto {
        return backend.post("/api/v1/customers", request)
    }

    suspend fun updateCustomer(
        customerId: Int,
        request: UpdateCustomerCommand
    ): CustomerDto {
        return backend.put("/api/v1/customers/$customerId", request)
    }
}