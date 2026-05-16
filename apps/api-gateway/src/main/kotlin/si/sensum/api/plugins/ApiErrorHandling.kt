package si.sensum.api.plugins

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import si.sensum.api.backend.BackendHttpException
import si.sensum.logging.Logger
import si.sensum.shared.models.api.ApiErrorResponse

fun Application.installApiErrorHandling() {
    install(StatusPages) {
        exception<BadRequestException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiErrorResponse(
                    error = "Invalid request body. Check that all required JSON fields are present and have the correct type."
                )
            )
        }

        exception<BackendHttpException> { call, cause ->
            val status = HttpStatusCode.fromValue(cause.statusCode)

            if (cause.responseBody.isNotBlank()) {
                call.respondText(
                    text = cause.responseBody,
                    status = status,
                    contentType = ContentType.Application.Json
                )
            } else {
                call.respond(
                    status,
                    ApiErrorResponse(
                        error = "Backend request failed with HTTP ${cause.statusCode}."
                    )
                )
            }
        }

        exception<Throwable> { call, cause ->
            Logger.log.error(cause) {
                "[HTTP] Unexpected api-gateway error"
            }

            call.respond(
                HttpStatusCode.InternalServerError,
                ApiErrorResponse(
                    error = "Unexpected API Gateway error. Check api-gateway logs for details."
                )
            )
        }
    }
}