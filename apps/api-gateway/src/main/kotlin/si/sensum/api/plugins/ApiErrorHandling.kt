package si.sensum.api.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import si.sensum.shared.http.ServiceHttpException
import si.sensum.shared.ktor.errors.installCommonErrorHandling
import si.sensum.shared.ktor.errors.respondApiError

fun Application.installApiErrorHandling() {
    installCommonErrorHandling(
        serviceName = "api-gateway",
        unexpectedErrorMessage = "Unexpected API Gateway error. Check api-gateway logs for details."
    ) {
        exception<ServiceHttpException> { call, cause ->
            val status = cause.statusCode

            if (cause.responseBody.isNotBlank()) {
                call.respondText(
                    text = cause.responseBody,
                    status = status,
                    contentType = ContentType.Application.Json
                )
            } else {
                call.respondApiError(
                    serviceName = "api-gateway",
                    status = status,
                    cause = cause,
                    logMessage = "Backend request failed with HTTP ${cause.statusCode}.",
                    responseMessage = "Backend request failed with HTTP ${cause.statusCode}."
                )
            }
        }
    }
}