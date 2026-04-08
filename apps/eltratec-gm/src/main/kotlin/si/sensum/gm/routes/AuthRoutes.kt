package si.sensum.gm.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.gm.services.AuthService
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.LoginRequest
import si.sensum.sws.SwsHttpException
import si.sensum.sws.SwsInvalidResponseException
import si.sensum.sws.SwsUnauthorizedException

fun Route.authRoutes(authService: AuthService) {
    route("/api/v1") {
        post("/auth/login") {
            val request = call.receive<LoginRequest>()

            try {
                val response = authService.login(
                    username = request.username,
                    password = request.password
                )

                call.respond(HttpStatusCode.OK, response)
            } catch (e: SwsUnauthorizedException) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiErrorResponse(error = e.message ?: "Unauthorized")
                )
            } catch (e: SwsInvalidResponseException) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    ApiErrorResponse(error = e.message ?: "Invalid response from SWS")
                )
            } catch (e: SwsHttpException) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    ApiErrorResponse(error = "SWS HTTP ${e.statusCode}: ${e.message ?: "SWS HTTP error"}")
                )
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiErrorResponse(error = "Internal server error")
                )
            }
        }
    }
}