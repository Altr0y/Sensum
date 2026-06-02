package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import si.sensum.backend.service.UserService
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.models.users.CreateUserCommand
import si.sensum.shared.models.users.UpdateUserCommand

fun Route.configureUserRoutes(
    userService: UserService
) {
    route("/api/v1/customers/{customerId}/users") {
        get {
            val customerId = call.requireIntPathParameter("customerId")

            val users = userService.getUsersByCustomer(customerId)

            call.respond(HttpStatusCode.OK, users)
        }

        post {
            val customerId = call.requireIntPathParameter("customerId")
            val request = call.receive<CreateUserCommand>()

            val created = userService.createUser(
                customerId = customerId,
                request = request
            )

            call.respond(HttpStatusCode.Created, created)
        }

        get("/{userId}") {
            val customerId = call.requireIntPathParameter("customerId")
            val userId = call.requireIntPathParameter("userId")

            val user = userService.getUser(
                customerId = customerId,
                userId = userId
            )

            call.respond(HttpStatusCode.OK, user)
        }

        put("/{userId}") {
            val customerId = call.requireIntPathParameter("customerId")
            val userId = call.requireIntPathParameter("userId")
            val request = call.receive<UpdateUserCommand>()

            val updated = userService.updateUser(
                customerId = customerId,
                userId = userId,
                request = request
            )

            call.respond(HttpStatusCode.OK, updated)
        }

        delete("/{userId}") {
            val customerId = call.requireIntPathParameter("customerId")
            val userId = call.requireIntPathParameter("userId")

            userService.deleteUser(
                customerId = customerId,
                userId = userId
            )

            call.respond(
                HttpStatusCode.OK,
                mapOf("deleted" to true)
            )
        }
    }
}