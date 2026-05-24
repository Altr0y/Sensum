package si.sensum.api.backend

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.api.customers.CreateCustomerRequest
import si.sensum.shared.models.api.customers.CustomerDto
import si.sensum.shared.models.api.customers.UpdateCustomerRequest

internal class BackendCustomerClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getCustomer(customerId: Int): CustomerDto {
        return backend.get("/api/v1/customers/$customerId")
    }

    suspend fun createCustomer(
        request: CreateCustomerRequest
    ): CustomerDto {
        return backend.post("/api/v1/customers", request)
    }

    suspend fun updateCustomer(
        customerId: Int,
        request: UpdateCustomerRequest
    ): CustomerDto {
        return backend.put("/api/v1/customers/$customerId", request)
    }
}