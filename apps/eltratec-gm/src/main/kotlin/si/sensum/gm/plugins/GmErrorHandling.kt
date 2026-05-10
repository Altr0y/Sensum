package si.sensum.gm.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import si.sensum.logging.Logger
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.sws.SwsHttpException
import si.sensum.sws.SwsInvalidResponseException
import si.sensum.sws.SwsUnauthorizedException

fun Application.installGmErrorHandling() {
    install(StatusPages) {
        exception<BadRequestException> { call, _ ->
            Logger.log.warn {
                "[HTTP] Bad request: Invalid request body"
            }

            call.respond(
                HttpStatusCode.BadRequest,
                ApiErrorResponse(error = "Invalid request body")
            )
        }

        exception<SwsUnauthorizedException> { call, _ ->
            Logger.log.warn {
                "[HTTP] Unauthorized: Invalid username or password"
            }

            call.respond(
                HttpStatusCode.Unauthorized,
                ApiErrorResponse(error = "Invalid username or password")
            )
        }

        exception<SwsInvalidResponseException> { call, cause ->
            val message = cause.message ?: "Invalid response from SWS"

            Logger.log.error {
                "[HTTP] Invalid SWS response: $message"
            }

            call.respond(
                HttpStatusCode.BadGateway,
                ApiErrorResponse(error = message)
            )
        }

        exception<SwsHttpException> { call, cause ->
            val message = "SWS HTTP ${cause.statusCode}: ${cause.message ?: "SWS HTTP error"}"

            Logger.log.error {
                "[HTTP] SWS HTTP error: $message"
            }

            call.respond(
                HttpStatusCode.BadGateway,
                ApiErrorResponse(error = message)
            )
        }

        exception<Throwable> { call, cause ->
            Logger.log.error(cause) {
                "[HTTP] Unexpected server error"
            }

            call.respond(
                HttpStatusCode.InternalServerError,
                ApiErrorResponse(error = "Internal server error")
            )
        }
    }
}