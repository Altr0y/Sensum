package si.sensum.backend.measurements

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.RefreshMeasurementsRequest

fun Route.measurementRoutes(
    repository: MeasurementRepository,
    refreshService: MeasurementRefreshService
) {
    route("/api/v1/measurements") {
        get {
            call.respond(repository.findAll())
        }

        post {
            val request = call.receive<MeasurementDto>()
            val created = repository.create(request)
            call.respond(HttpStatusCode.Created, created)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("Invalid measurement id"))
                return@put
            }

            val request = call.receive<MeasurementDto>()
            val updated = repository.update(id, request)

            if (updated == null) {
                call.respond(HttpStatusCode.NotFound, ApiErrorResponse("Measurement not found"))
            } else {
                call.respond(HttpStatusCode.OK, updated)
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiErrorResponse("Invalid measurement id"))
                return@delete
            }

            val deleted = repository.delete(id)

            if (!deleted) {
                call.respond(HttpStatusCode.NotFound, ApiErrorResponse("Measurement not found"))
            } else {
                call.respond(HttpStatusCode.NoContent)
            }
        }

        delete {
            repository.deleteAll()
            call.respond(HttpStatusCode.NoContent)
        }

        post("/refresh") {
            val request = call.receive<RefreshMeasurementsRequest>()
            val response = refreshService.refresh(request)
            call.respond(HttpStatusCode.OK, response)
        }
    }
}