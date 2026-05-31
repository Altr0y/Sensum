package si.sensum.api.routes

import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import si.sensum.api.auth.requireAdminUser
import si.sensum.api.backend.BackendUserClient
import si.sensum.shared.ktor.response.respondCreated
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.models.users.CreateUserCommand
import si.sensum.shared.models.users.UpdateUserCommand

internal fun Route.userRoutes(
    backendUsers: BackendUserClient
) {
    route("/users") {
        get {
            val principal = call.requireAdminUser() ?: return@get

            call.respondOk {
                backendUsers.getUsers(principal.customerId)
            }
        }

        post {
            val principal = call.requireAdminUser() ?: return@post
            val request = call.receive<CreateUserCommand>()

            call.respondCreated {
                backendUsers.createUser(
                    customerId = principal.customerId,
                    request = request
                )
            }
        }

        get("/{userId}") {
            val principal = call.requireAdminUser() ?: return@get
            val userId = call.requireIntPathParameter("userId")

            call.respondOk {
                backendUsers.getUser(
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
                backendUsers.updateUser(
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
                backendUsers.deleteUser(
                    customerId = principal.customerId,
                    userId = userId
                )

                mapOf("deleted" to true)
            }
        }
    }
}