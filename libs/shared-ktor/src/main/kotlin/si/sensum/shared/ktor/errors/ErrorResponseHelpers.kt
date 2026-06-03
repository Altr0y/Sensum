package si.sensum.shared.ktor.errors

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.callid.callId
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respond
import si.sensum.logging.Logger
import si.sensum.logging.shortRequestId
import si.sensum.shared.models.common.ApiError

suspend fun ApplicationCall.respondApiWarn(
    serviceName: String,
    status: HttpStatusCode,
    message: String
) {
    Logger.log.warn {
        apiLogMessage(
            serviceName = serviceName,
            level = "WARN",
            status = status,
            message = message
        )
    }

    respond(
        status = status,
        message = ApiError(error = message)
    )
}

suspend fun ApplicationCall.respondApiWarn(
    serviceName: String,
    status: HttpStatusCode,
    logMessage: String,
    responseMessage: String
) {
    Logger.log.warn {
        apiLogMessage(
            serviceName = serviceName,
            level = "WARN",
            status = status,
            message = logMessage
        )
    }

    respond(
        status = status,
        message = ApiError(error = responseMessage)
    )
}

suspend fun ApplicationCall.respondApiError(
    serviceName: String,
    status: HttpStatusCode,
    cause: Throwable,
    message: String
) {
    respondApiError(
        serviceName = serviceName,
        status = status,
        cause = cause,
        logMessage = message,
        responseMessage = message
    )
}

suspend fun ApplicationCall.respondApiError(
    serviceName: String,
    status: HttpStatusCode,
    cause: Throwable,
    logMessage: String,
    responseMessage: String
) {
    Logger.log.error(cause) {
        apiLogMessage(
            serviceName = serviceName,
            level = "ERROR",
            status = status,
            message = logMessage
        )
    }

    respond(
        status = status,
        message = ApiError(error = responseMessage)
    )
}

private fun ApplicationCall.apiLogMessage(
    serviceName: String,
    level: String,
    status: HttpStatusCode,
    message: String
): String {
    val requestId = callId.shortRequestId()
    val method = request.httpMethod.value
    val path = "/" + request.path().trimStart('/')

    return "[$level] service=$serviceName requestId=$requestId method=$method path=$path status=${status.value} message=$message"
}