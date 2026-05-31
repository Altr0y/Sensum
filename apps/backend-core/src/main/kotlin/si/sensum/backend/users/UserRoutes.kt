package si.sensum.backend.users

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.models.users.CreateUserCommand
import si.sensum.shared.models.users.UpdateUserCommand

fun Route.userRoutes(
    userRepository: UserRepository
) {
    route("/api/v1/customers/{customerId}/users") {
        get {
            val customerId = call.requireIntPathParameter("customerId")

            val users = userRepository
                .findByCustomerId(customerId)
                .map { it.toDto() }

            call.respond(HttpStatusCode.OK, users)
        }

        post {
            val customerId = call.requireIntPathParameter("customerId")
            val request = call.receive<CreateUserCommand>()

            require(request.username.isNotBlank()) {
                "Username must not be blank"
            }

            require(request.password.isNotBlank()) {
                "Password must not be blank"
            }

            val created = userRepository.create(
                customerId = customerId,
                username = request.username,
                passwordHash = request.password, // TODO : kasneje hash
                role = request.role.toDomain(),
                enabled = request.enabled
            )

            call.respond(HttpStatusCode.Created, created.toDto())
        }

        get("/{userId}") {
            val customerId = call.requireIntPathParameter("customerId")
            val userId = call.requireIntPathParameter("userId")

            val user = userRepository.findByCustomerAndId(
                customerId = customerId,
                userId = userId
            ) ?: error("User not found")

            call.respond(HttpStatusCode.OK, user.toDto())
        }

        put("/{userId}") {
            val customerId = call.requireIntPathParameter("customerId")
            val userId = call.requireIntPathParameter("userId")
            val request = call.receive<UpdateUserCommand>()

            val updated = userRepository.update(
                customerId = customerId,
                userId = userId,
                username = request.username,
                passwordHash = request.password,
                role = request.role?.toDomain(),
                enabled = request.enabled
            ) ?: error("User not found")

            call.respond(HttpStatusCode.OK, updated.toDto())
        }

        delete("/{userId}") {
            val customerId = call.requireIntPathParameter("customerId")
            val userId = call.requireIntPathParameter("userId")

            val deleted = userRepository.delete(
                customerId = customerId,
                userId = userId
            )

            if (!deleted) {
                error("User not found")
            }

            call.respond(
                HttpStatusCode.OK,
                mapOf("deleted" to true)
            )
        }
    }
}