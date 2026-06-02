package si.sensum.gm.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import si.sensum.gm.errors.GmApiException
import si.sensum.shared.ktor.errors.respondApiError
import si.sensum.shared.ktor.errors.respondApiWarn
import si.sensum.sws.SwsHttpException
import si.sensum.sws.SwsInvalidResponseException
import si.sensum.sws.SwsTimeoutException
import si.sensum.sws.SwsUnauthorizedException

private const val SERVICE_NAME = "eltratec-gm"

internal fun Application.installGmErrorHandling() {
    install(StatusPages) {
        exception<GmApiException> { call, cause ->
            call.respondApiWarn(
                serviceName = SERVICE_NAME,
                status = cause.statusCode,
                message = cause.message
            )
        }

        exception<BadRequestException> { call, cause ->
            call.respondApiWarn(
                serviceName = SERVICE_NAME,
                status = HttpStatusCode.BadRequest,
                logMessage = cause.message ?: "Invalid request body",
                responseMessage = "Invalid request body. Check that JSON is valid and all required fields have correct types."
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respondApiWarn(
                serviceName = SERVICE_NAME,
                status = HttpStatusCode.BadRequest,
                message = cause.message ?: "Invalid request."
            )
        }

        exception<SwsUnauthorizedException> { call, cause ->
            call.respondApiWarn(
                serviceName = SERVICE_NAME,
                status = HttpStatusCode.Unauthorized,
                message = cause.message ?: "SWS authorization failed"
            )
        }

        exception<SwsInvalidResponseException> { call, cause ->
            call.respondApiError(
                serviceName = SERVICE_NAME,
                status = HttpStatusCode.BadGateway,
                cause = cause,
                message = cause.message ?: "Invalid response from SWS"
            )
        }

        exception<SwsTimeoutException> { call, cause ->
            call.respondApiError(
                serviceName = SERVICE_NAME,
                status = HttpStatusCode.GatewayTimeout,
                cause = cause,
                message = cause.message ?: "SWS request timed out"
            )
        }

        exception<SwsHttpException> { call, cause ->
            call.respondApiError(
                serviceName = SERVICE_NAME,
                status = HttpStatusCode.BadGateway,
                cause = cause,
                message = swsHttpErrorMessage(cause)
            )
        }

        exception<Throwable> { call, cause ->
            call.respondApiError(
                serviceName = SERVICE_NAME,
                status = HttpStatusCode.InternalServerError,
                cause = cause,
                logMessage = cause.message ?: "Unexpected server error",
                responseMessage = "Internal server error"
            )
        }
    }
}

private fun swsHttpErrorMessage(cause: SwsHttpException): String {
    return when (cause.statusCode) {
        401 -> "SWS session is unauthorized or expired."
        500 -> "SWS rejected the SOAP request. Check station/channel pairs, datetime format, and whether the SWS operation is supported."
        else -> "SWS returned HTTP ${cause.statusCode}: ${cause.message ?: "SWS HTTP error"}"
    }
}