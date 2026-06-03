package si.sensum.api.controller

import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import si.sensum.api.domain.requireAdminUser
import si.sensum.api.domain.requireAuthenticatedUser
import si.sensum.api.service.CustomerService
import si.sensum.shared.ktor.response.respondCreated
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.customers.CreateCustomerCommand
import si.sensum.shared.models.customers.UpdateCustomerCommand

internal fun Route.customerController(
    customerService: CustomerService
) {
    route("/customers") {

        post {
            call.requireAdminUser() ?: return@post

            val request = call.receive<CreateCustomerCommand>()

            call.respondCreated {
                customerService.createCustomer(request)
            }
        }

        get("/me") {
            val principal = call.requireAuthenticatedUser() ?: return@get

            call.respondOk {
                customerService.getCustomer(
                    customerId = principal.customerId
                )
            }
        }

        put("/me") {
            val principal = call.requireAdminUser() ?: return@put

            val request = call.receive<UpdateCustomerCommand>()

            call.respondOk {
                customerService.updateCustomer(
                    customerId = principal.customerId,
                    request = request
                )
            }
        }
    }
}