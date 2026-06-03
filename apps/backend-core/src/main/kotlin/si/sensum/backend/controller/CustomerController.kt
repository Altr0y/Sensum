package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import si.sensum.backend.service.CustomerService
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.models.customers.CreateCustomerCommand
import si.sensum.shared.models.customers.UpdateCustomerCommand

fun Route.configureCustomerRoutes(
    customerService: CustomerService
) {
    route("/api/v1/customers") {
        post {
            val request = call.receive<CreateCustomerCommand>()

            val created = customerService.createCustomer(request)

            call.respond(HttpStatusCode.Created, created)
        }
    }

    route("/api/v1/customers/{customerId}") {
        get {
            val customerId = call.requireIntPathParameter("customerId")

            val customer = customerService.getCustomer(customerId)

            call.respond(HttpStatusCode.OK, customer)
        }

        put {
            val customerId = call.requireIntPathParameter("customerId")
            val request = call.receive<UpdateCustomerCommand>()

            val updated = customerService.updateCustomer(
                customerId = customerId,
                request = request
            )

            call.respond(HttpStatusCode.OK, updated)
        }
    }
}