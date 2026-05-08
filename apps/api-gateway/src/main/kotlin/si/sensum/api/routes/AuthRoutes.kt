package si.sensum.api.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.api.gm.GmClient
import si.sensum.api.gm.GmClientException
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.LoginRequest

fun Route.authRoutes(gmClient: GmClient) {
    route("/api/v1") {
        post("/auth/login") {
            val request = call.receive<LoginRequest>()

            try {
                val response = gmClient.login(request)
                call.respond(HttpStatusCode.OK, response)

            } catch (e: GmClientException) {
                call.respond(
                    e.status,
                    ApiErrorResponse(error = e.message ?: "GM authentication failed")
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiErrorResponse(error = "Internal server error")
                )
            }
        }
    }
}