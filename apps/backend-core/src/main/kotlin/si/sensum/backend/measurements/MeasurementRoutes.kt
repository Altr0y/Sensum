package si.sensum.backend.measurements

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest
import si.sensum.shared.models.datetime.ApiDateTime
import kotlinx.datetime.LocalDateTime

fun Route.measurementRoutes(
    repository: MeasurementRepository,
    refreshService: MeasurementRefreshService
) {
    val service = MeasurementService()
    route("/api/measurements") {
        get {
            val channelId = call.request.queryParameters["channelId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or invalid channelId")

            val (from, to) = call.parseDateRange()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing 'from'/'to' parameters, use ISO-8601 e.g. 2026-01-01T00:00:00"
                )

            call.respond(service.getMeasurements(channelId, from, to))
        }

        post("/regenerate") {
            val (from, to) = call.parseDateRange()
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing 'from'/'to' parameters, use ISO-8601 e.g. 2026-01-01T00:00:00"
                )

            service.regenerateMeasurements(from, to)
            call.respond(HttpStatusCode.OK, "Regenerated measurements from $from to $to")
        }

        post("/clear") {
            val (from, to) = call.parseDateRange()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing 'from'/'to' parameters")

            service.clearSimulated(from, to)
            call.respond(HttpStatusCode.OK, "Cleared measurements from $from to $to")
        }
    }
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
            val request = call.receive<MeasurementsByStationChannelPairsRequest>()

            if (request.stationChannelPairs.isEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiErrorResponse("Missing required field: stationChannelPairs")
                )
                return@post
            }

            val normalizedRequest = try {
                request.copy(
                    datetimeFrom = ApiDateTime.requireNormalized("datetimeFrom", request.datetimeFrom),
                    datetimeTo = ApiDateTime.requireNormalized("datetimeTo", request.datetimeTo)
                )
            } catch (error: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiErrorResponse(error.message ?: "Invalid datetime format")
                )
                return@post
            }

            val response = refreshService.refresh(normalizedRequest)
            call.respond(HttpStatusCode.OK, response)
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.parseDateRange(): Pair<LocalDateTime, LocalDateTime>? {
    val from = runCatching {
        LocalDateTime.parse(request.queryParameters["from"] ?: return null)
    }.getOrElse { return null }

    val to = runCatching {
        LocalDateTime.parse(request.queryParameters["to"] ?: return null)
    }.getOrElse { return null }

    return Pair(from, to)
}