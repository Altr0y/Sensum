package si.sensum.gm.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respond
import si.sensum.gm.errors.GmApiException
import si.sensum.logging.Logger
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.sws.SwsHttpException
import si.sensum.sws.SwsInvalidResponseException
import si.sensum.sws.SwsTimeoutException
import si.sensum.sws.SwsUnauthorizedException

internal fun Application.installGmErrorHandling() {
    install(StatusPages) {
        exception<GmApiException> { call, cause ->
            call.respondWarn(
                status = cause.statusCode,
                message = cause.message
            )
        }

        exception<BadRequestException> { call, cause ->
            call.respondWarn(
                status = HttpStatusCode.BadRequest,
                logMessage = cause.message ?: "Invalid request body",
                responseMessage = "Invalid request body. Check that JSON is valid and all required fields have correct types."
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respondWarn(
                status = HttpStatusCode.BadRequest,
                message = cause.message ?: "Invalid request."
            )
        }

        exception<SwsUnauthorizedException> { call, cause ->
            call.respondWarn(
                status = HttpStatusCode.Unauthorized,
                message = cause.message ?: "SWS authorization failed"
            )
        }

        exception<SwsInvalidResponseException> { call, cause ->
            call.respondError(
                status = HttpStatusCode.BadGateway,
                cause = cause,
                message = cause.message ?: "Invalid response from SWS"
            )
        }

        exception<SwsTimeoutException> { call, cause ->
            call.respondError(
                status = HttpStatusCode.GatewayTimeout,
                cause = cause,
                message = cause.message ?: "SWS request timed out"
            )
        }

        exception<SwsHttpException> { call, cause ->
            call.respondError(
                status = HttpStatusCode.BadGateway,
                cause = cause,
                message = swsHttpErrorMessage(cause)
            )
        }

        exception<Throwable> { call, cause ->
            call.respondError(
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

private suspend fun ApplicationCall.respondWarn(
    status: HttpStatusCode,
    message: String
) {
    Logger.log.warn {
        gmLogMessage(
            level = "WARN",
            status = status,
            message = message
        )
    }

    respond(
        status,
        ApiErrorResponse(error = message)
    )
}

private suspend fun ApplicationCall.respondWarn(
    status: HttpStatusCode,
    logMessage: String,
    responseMessage: String
) {
    Logger.log.warn {
        gmLogMessage(
            level = "WARN",
            status = status,
            message = logMessage
        )
    }

    respond(
        status,
        ApiErrorResponse(error = responseMessage)
    )
}

private suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    cause: Throwable,
    message: String
) {
    respondError(
        status = status,
        cause = cause,
        logMessage = message,
        responseMessage = message
    )
}

private suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    cause: Throwable,
    logMessage: String,
    responseMessage: String
) {
    Logger.log.error(cause) {
        gmLogMessage(
            level = "ERROR",
            status = status,
            message = logMessage
        )
    }

    respond(
        status,
        ApiErrorResponse(error = responseMessage)
    )
}

private fun ApplicationCall.gmLogMessage(
    level: String,
    status: HttpStatusCode,
    message: String
): String {
    val requestId = callId ?: "-"
    val method = request.httpMethod.value
    val path = "/" + request.path().trimStart('/')

    return "[$level] service=eltratec-gm requestId=$requestId method=$method path=$path status=${status.value} message=$message"
}