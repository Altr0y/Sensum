package si.sensum.api.controller

import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import si.sensum.api.domain.requireAdminUser
import si.sensum.api.service.UserService
import si.sensum.shared.ktor.response.respondCreated
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.models.users.CreateUserCommand
import si.sensum.shared.models.users.UpdateUserCommand

internal fun Route.userController(
    userService: UserService
) {
    route("/users") {
        get {
            val principal = call.requireAdminUser() ?: return@get

            call.respondOk {
                userService.getUsers(
                    customerId = principal.customerId
                )
            }
        }

        post {
            val principal = call.requireAdminUser() ?: return@post
            val request = call.receive<CreateUserCommand>()

            call.respondCreated {
                userService.createUser(
                    customerId = principal.customerId,
                    request = request
                )
            }
        }

        get("/{userId}") {
            val principal = call.requireAdminUser() ?: return@get
            val userId = call.requireIntPathParameter("userId")

            call.respondOk {
                userService.getUser(
                    customerId = principal.customerId,
                    userId = userId
                )
            }
        }

        put("/{userId}") {
            val principal = call.requireAdminUser() ?: return@put
            val userId = call.requireIntPathParameter("userId")
            val request = call.receive<UpdateUserCommand>()

            call.respondOk {
                userService.updateUser(
                    customerId = principal.customerId,
                    userId = userId,
                    request = request
                )
            }
        }

        delete("/{userId}") {
            val principal = call.requireAdminUser() ?: return@delete
            val userId = call.requireIntPathParameter("userId")

            call.respondOk {
                userService.deleteUser(
                    customerId = principal.customerId,
                    userId = userId
                )

                mapOf("deleted" to true)
            }
        }
    }
}