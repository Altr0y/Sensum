package si.sensum.backend.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import org.postgresql.util.PSQLException
import si.sensum.shared.ktor.errors.installCommonErrorHandling
import si.sensum.shared.ktor.errors.respondApiError
import si.sensum.shared.ktor.errors.respondApiWarn

fun Application.installBackendErrorHandling() {
    installCommonErrorHandling(
        serviceName = "backend-core",
        unexpectedErrorMessage = "Unexpected backend error. Check backend-core logs for details."
    ) {
        exception<PSQLException> { call, cause ->
            call.respondApiError(
                serviceName = "backend-core",
                status = HttpStatusCode.InternalServerError,
                cause = cause,
                logMessage = "PostgreSQL error",
                responseMessage = "Database error. The database schema may be outdated. Check backend-core logs."
            )
        }

        exception<IllegalStateException> { call, cause ->
            val message = cause.message.orEmpty()

            val status = when {
                message.contains("GM measurements failed", ignoreCase = true) -> HttpStatusCode.BadGateway
                message.contains("GM login failed", ignoreCase = true) -> HttpStatusCode.BadGateway
                else -> HttpStatusCode.InternalServerError
            }

            call.respondApiWarn(
                serviceName = "backend-core",
                status = status,
                message = cleanInternalMessage(message)
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