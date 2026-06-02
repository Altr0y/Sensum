package si.sensum.shared.ktor.errors

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig

fun Application.installCommonErrorHandling(
    serviceName: String,
    unexpectedErrorMessage: String = "Unexpected server error. Check service logs for details.",
    customHandlers: StatusPagesConfig.() -> Unit = {}
) {
    install(StatusPages) {
        customHandlers()

        exception<ApiException> { call, cause ->
            call.respondApiWarn(
                serviceName = serviceName,
                status = cause.statusCode,
                message = cause.message
            )
        }

        exception<BadRequestException> { call, cause ->
            call.respondApiWarn(
                serviceName = serviceName,
                status = HttpStatusCode.BadRequest,
                logMessage = cause.message ?: "Invalid request body",
                responseMessage = "Invalid request body. Check that JSON is valid and all required fields have correct types."
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respondApiWarn(
                serviceName = serviceName,
                status = HttpStatusCode.BadRequest,
                message = cause.message ?: "Invalid request."
            )
        }

        exception<Throwable> { call, cause ->
            call.respondApiError(
                serviceName = serviceName,
                status = HttpStatusCode.InternalServerError,
                cause = cause,
                logMessage = cause.message ?: "Unexpected server error",
                responseMessage = unexpectedErrorMessage
            )
        }
    }
}