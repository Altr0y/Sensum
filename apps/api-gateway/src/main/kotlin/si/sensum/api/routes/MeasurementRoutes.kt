package si.sensum.api.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.api.backend.BackendClient
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest

fun Route.measurementRoutes(
    backendClient: BackendClient
) {
    route("/api/v1/measurements") {
        get {
            val json = backendClient.getMeasurementsJson()

            call.respondText(
                text = json,
                contentType = ContentType.Application.Json
            )
        }

        post {
            val request = call.receive<MeasurementDto>()
            val response = backendClient.createMeasurement(request)

            call.respond(HttpStatusCode.Created, response)
        }

        put("/{id}") {
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
            val id = call.parameters["id"]?.toLongOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("Invalid measurement id"))
                return@delete
            }

            backendClient.deleteMeasurement(id)
            call.respond(HttpStatusCode.NoContent)
        }

        delete {
            backendClient.deleteAllMeasurements()
            call.respond(HttpStatusCode.NoContent)
        }

        post("/refresh") {
            val request = call.receive<MeasurementsByStationChannelPairsRequest>()
            val response = backendClient.refreshMeasurements(request)

            call.respond(HttpStatusCode.OK, response)
        }
    }
}