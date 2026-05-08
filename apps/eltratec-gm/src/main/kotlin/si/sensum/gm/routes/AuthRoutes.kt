package si.sensum.gm.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.gm.services.AuthService
import si.sensum.gm.auth.requireGmBearerToken
import si.sensum.shared.auth.bearer.TokenValidator
import si.sensum.logging.Logger
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.LoginRequest
import si.sensum.sws.SwsHttpException
import si.sensum.sws.SwsInvalidResponseException
import si.sensum.sws.SwsUnauthorizedException

private val log = Logger.log

fun Route.authRoutes(
    authService: AuthService,
    tokenValidator: TokenValidator
) {
    route("/api/v1") {
        post("/auth/login") {
            val request = call.receive<LoginRequest>()

            try {
                val response = authService.login(
                    username = request.username,
                    password = request.password
                )

                log.info { "[HTTP] Auth login success for user=${request.username}" }

                call.respond(HttpStatusCode.OK, response)

            } catch (e: SwsUnauthorizedException) {
                log.warn { "[HTTP] Unauthorized login for user=${request.username}: ${e.message}" }

                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiErrorResponse(error = e.message ?: "Unauthorized")
                )

            } catch (e: SwsInvalidResponseException) {
                log.error { "[HTTP] Invalid SWS response for user=${request.username}: ${e.message}" }

                call.respond(
                    HttpStatusCode.BadGateway,
                    ApiErrorResponse(error = e.message ?: "Invalid response from SWS")
                )

            } catch (e: SwsHttpException) {
                log.error {
                    "[HTTP] SWS HTTP error for user=${request.username}: status=${e.statusCode}, message=${e.message}"
                }

                call.respond(
                    HttpStatusCode.BadGateway,
                    ApiErrorResponse(error = "SWS HTTP ${e.statusCode}: ${e.message ?: "SWS HTTP error"}")
                )

            } catch (e: Exception) {
                log.error(e) { "[HTTP] Unexpected error for user=${request.username}" }

                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiErrorResponse(error = "Internal server error")
                )
            }
        }
        get("/protected") {
            if (!call.requireGmBearerToken(tokenValidator)) return@get

            call.respond(
                mapOf(
                    "status" to "ok",
                    "message" to "Authorized GM request"
                )
            )
        }
    }
}