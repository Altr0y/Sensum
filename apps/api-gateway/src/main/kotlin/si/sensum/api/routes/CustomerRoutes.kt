package si.sensum.api.routes

import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import si.sensum.api.auth.requireAdminUser
import si.sensum.api.auth.requireAuthenticatedUser
import si.sensum.api.backend.BackendCustomerClient
import si.sensum.shared.ktor.response.respondCreated
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.customers.CreateCustomerCommand
import si.sensum.shared.models.customers.UpdateCustomerCommand

internal fun Route.customerRoutes(
    customers: BackendCustomerClient
) {
    route("/customers") {

        post {
            call.requireAdminUser() ?: return@post

            val request = call.receive<CreateCustomerCommand>()

            require(request.name.isNotBlank()) {
                "Customer name must not be blank"
            }

            call.respondCreated {
                customers.createCustomer(request)
            }
        }

        get("/me") {
            val principal = call.requireAuthenticatedUser() ?: return@get

            call.respondOk {
                customers.getCustomer(
                    customerId = principal.customerId
                )
            }
        }

        put("/me") {
            val principal = call.requireAdminUser() ?: return@put

            val request = call.receive<UpdateCustomerCommand>()

            require(request.name.isNotBlank()) {
                "Customer name must not be blank"
            }

            call.respondOk {
                customers.updateCustomer(
                    customerId = principal.customerId,
                    request = request
                )
            }
        }
    }
}