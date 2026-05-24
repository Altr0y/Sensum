package si.sensum.backend.measurements

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest
import si.sensum.shared.models.datetime.ApiDateTime

fun Route.measurementRoutes(
    measurementRepository: MeasurementRepository,
    refreshService: MeasurementRefreshService
) {
    val service = MeasurementService(repository = measurementRepository)
    route("/api/v1/measurements") {
        get {
            call.respondOk {
                measurementRepository.findAll()
            }
        }


        get("/range") {
            val channelId = call.request.queryParameters["channelId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Missing or invalid query parameter: channelId")

            val (from, to) = call.parseDateRange()

            call.respondOk {
                service.getMeasurements(
                    channelId = channelId,
                    from = from,
                    to = to
                )
            }
        }

        post {
            val request = call.receive<MeasurementDto>()

            call.respond(
                status = HttpStatusCode.Created,
                message = measurementRepository.create(request)
            )
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
                    datetimeFrom = ApiDateTime.requireNormalizedLocal(
                        fieldName = "datetimeFrom",
                        value = request.datetimeFrom
                    ),
                    datetimeTo = ApiDateTime.requireNormalizedLocal(
                        fieldName = "datetimeTo",
                        value = request.datetimeTo
                    )
                )
            } catch (error: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiErrorResponse(error.message ?: "Invalid datetime format")
                )
                return@post
            }

            call.respondOk {
                refreshService.refresh(normalizedRequest)
            }
        }

        post("/regenerate") {
            val (from, to) = call.parseDateRange()

            service.regenerateMeasurements(
                from = from,
                to = to
            )

            call.respondOk {
                mapOf("regenerated" to true)
            }
        }

        put("/{id}") {
            val id = call.requireMeasurementId()
            val request = call.receive<MeasurementDto>()
            val updated = measurementRepository.update(id, request)

            if (updated == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiErrorResponse("Measurement not found")
                )
                return@put
            }

            call.respondOk {
                updated
            }
        }

        delete("/range") {
            val (from, to) = call.parseDateRange()
            val deleted = measurementRepository.deleteByRange(from, to)

            call.respondOk {
                mapOf("deleted" to deleted)
            }
        }


        delete("/{id}") {
            val id = call.requireMeasurementId()
            val deleted = measurementRepository.delete(id)

            if (!deleted) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiErrorResponse("Measurement not found")
                )
                return@delete
            }

            call.respondOk {
                mapOf("deleted" to true)
            }
        }

        delete {
            measurementRepository.deleteAll()

            call.respondOk {
                mapOf("deletedAll" to true)
            }
        }
    }
}

private fun ApplicationCall.requireMeasurementId(): Long {
    return parameters["id"]?.toLongOrNull()
        ?: throw IllegalArgumentException("Invalid measurement id")
}

private fun ApplicationCall.parseDateRange(): Pair<LocalDateTime, LocalDateTime> {
    val fromRaw = request.queryParameters["from"]
        ?: throw IllegalArgumentException("Missing required query parameter: from")

    val toRaw = request.queryParameters["to"]
        ?: throw IllegalArgumentException("Missing required query parameter: to")

    val from = LocalDateTime.parse(
        ApiDateTime.requireNormalizedLocal(
            fieldName = "from",
            value = fromRaw
        )
    )

    val to = LocalDateTime.parse(
        ApiDateTime.requireNormalizedLocal(
            fieldName = "to",
            value = toRaw
        )
    )

    return from to to
}