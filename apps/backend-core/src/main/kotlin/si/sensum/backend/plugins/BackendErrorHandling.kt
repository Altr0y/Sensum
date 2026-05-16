package si.sensum.backend.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.postgresql.util.PSQLException
import si.sensum.logging.Logger
import si.sensum.shared.models.api.ApiErrorResponse

fun Application.installBackendErrorHandling() {
    install(StatusPages) {
        exception<BadRequestException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiErrorResponse(
                    error = "Invalid request body. Check that all required JSON fields are present and have the correct type."
                )
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiErrorResponse(
                    error = cause.message ?: "Invalid request."
                )
            )
        }

        exception<PSQLException> { call, cause ->
            Logger.log.error(cause) {
                "[DB] PostgreSQL error"
            }

            call.respond(
                HttpStatusCode.InternalServerError,
                ApiErrorResponse(
                    error = "Database error. The database schema may be outdated. Check that measurements.status is boolean and measurements.value is real/float."
                )
            )
        }

        exception<IllegalStateException> { call, cause ->
            val message = cause.message.orEmpty()

            val status = when {
                message.contains("GM measurements failed", ignoreCase = true) -> HttpStatusCode.BadGateway
                message.contains("GM login failed", ignoreCase = true) -> HttpStatusCode.BadGateway
                else -> HttpStatusCode.InternalServerError
            }

            call.respond(
                status,
                ApiErrorResponse(
                    error = cleanInternalMessage(message)
                )
            )
        }

        exception<Throwable> { call, cause ->
            Logger.log.error(cause) {
                "[HTTP] Unexpected backend-core error"
            }

            call.respond(
                HttpStatusCode.InternalServerError,
                ApiErrorResponse(
                    error = "Unexpected backend error. Check backend-core logs for details."
                )
            )
        }
    }
}

private fun cleanInternalMessage(message: String): String {
    return when {
        message.contains("SWS HTTP 500", ignoreCase = true) ->
            "SWS rejected the measurement request. Check station/channel pairs and date format. Expected datetime format is usually yyyy-MM-dd HH:mm:ss."

        message.contains("GM measurements failed", ignoreCase = true) ->
            "GM measurements request failed. Check GM logs and SWS request parameters."

        message.contains("GM login failed", ignoreCase = true) ->
            "Backend could not log in to GM. Check SERVICE JWT config and SWS credentials."

        message.isBlank() ->
            "Backend request failed."

        else -> message
    }
}