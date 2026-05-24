package si.sensum.backend.customers

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.models.api.customers.CreateCustomerRequest
import si.sensum.shared.models.api.customers.UpdateCustomerRequest

fun Route.customerRoutes(
    customerRepository: CustomerRepository
) {
    fun Route.customerRoutes(
        customerRepository: CustomerRepository
    ) {
        route("/api/v1/customers") {
            post {
                val request = call.receive<CreateCustomerRequest>()

                require(request.name.isNotBlank()) {
                    "Customer name must not be blank"
                }

                val created = customerRepository.create(
                    name = request.name
                )

                call.respond(HttpStatusCode.Created, created.toDto())
            }
        }

        route("/api/v1/customers/{customerId}") {
            get {
                val customerId = call.requireIntPathParameter("customerId")

                val customer = customerRepository.findById(customerId)
                    ?: error("Customer not found")

                call.respond(HttpStatusCode.OK, customer.toDto())
            }

            put {
                val customerId = call.requireIntPathParameter("customerId")
                val request = call.receive<UpdateCustomerRequest>()

                require(request.name.isNotBlank()) {
                    "Customer name must not be blank"
                }

                val updated = customerRepository.update(
                    customerId = customerId,
                    name = request.name
                ) ?: error("Customer not found")

                call.respond(HttpStatusCode.OK, updated.toDto())
            }
        }
    }
}