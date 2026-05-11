package si.sensum.api.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.api.auth.requireApiBearerToken
import si.sensum.api.backend.BackendClient
import si.sensum.shared.auth.bearer.TokenValidator
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.RefreshMeasurementsRequest

fun Route.measurementRoutes(
    backendClient: BackendClient,
    tokenValidator: TokenValidator
) {
    route("/api/v1/measurements") {
        get {
            if (!call.requireApiBearerToken(tokenValidator)) return@get

            val json = backendClient.getMeasurementsJson()

            call.respondText(
                text = json,
                contentType = ContentType.Application.Json
            )
        }

        post {
            if (!call.requireApiBearerToken(tokenValidator)) return@post

            val request = call.receive<MeasurementDto>()
            val response = backendClient.createMeasurement(request)

            call.respond(HttpStatusCode.Created, response)
        }

        put("/{id}") {
            if (!call.requireApiBearerToken(tokenValidator)) return@put

            val id = call.parameters["id"]?.toLongOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("Invalid measurement id"))
                return@put
            }

            val request = call.receive<MeasurementDto>()
            val response = backendClient.updateMeasurement(id, request)

            call.respond(HttpStatusCode.OK, response)
        }

        delete("/{id}") {
            if (!call.requireApiBearerToken(tokenValidator)) return@delete

            val id = call.parameters["id"]?.toLongOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("Invalid measurement id"))
                return@delete
            }

            backendClient.deleteMeasurement(id)
            call.respond(HttpStatusCode.NoContent)
        }

        delete {
            if (!call.requireApiBearerToken(tokenValidator)) return@delete

            backendClient.deleteAllMeasurements()
            call.respond(HttpStatusCode.NoContent)
        }

        post("/refresh") {
            if (!call.requireApiBearerToken(tokenValidator)) return@post

            val request = call.receive<RefreshMeasurementsRequest>()
            val response = backendClient.refreshMeasurements(request)

            call.respond(HttpStatusCode.OK, response)
        }
    }
}