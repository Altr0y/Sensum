package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.datetime.LocalDateTime
import si.sensum.backend.service.MeasurementRefreshService
import si.sensum.backend.service.MeasurementService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.common.ApiError
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.shared.models.measurements.CreateMeasurementsBatchCommand
import si.sensum.shared.models.measurements.CreateMeasurementsBatchResult
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand

fun Route.configureMeasurementRoutes(
    measurementService: MeasurementService,
    refreshService: MeasurementRefreshService
) {
    route("/api/v1/measurements") {
        get {
            call.respondOk {
                measurementService.getAllMeasurements()
            }
        }

        get("/range") {
            val channelId = call.request.queryParameters["channelId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Missing or invalid query parameter: channelId")

            val (from, to) = call.parseDateRange()

            call.respondOk {
                measurementService.getMeasurements(
                    channelId = channelId,
                    from = from,
                    to = to
                )
            }
        }

        post {
            val request = call.receive<MeasurementDto>()

            val created = measurementService.createMeasurement(request)

            call.respond(
                status = HttpStatusCode.Created,
                message = created
            )
        }

        post("/batch") {
            val request = call.receive<CreateMeasurementsBatchCommand>()

            val insertedCount = measurementService.createMeasurementsBatch(
                measurements = request.measurements
            )

            call.respond(
                HttpStatusCode.Created,
                CreateMeasurementsBatchResult(
                    insertedCount = insertedCount
                )
            )
        }

        post("/refresh") {
            val request = call.receive<RefreshMeasurementsCommand>()

            if (request.stationChannelPairs.isEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError("Missing required field: stationChannelPairs")
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
                    ApiError(error.message ?: "Invalid datetime format")
                )
                return@post
            }

            call.respondOk {
                refreshService.refresh(normalizedRequest)
            }
        }

        post("/regenerate") {
            val (from, to) = call.parseDateRange()

            measurementService.regenerateMeasurements(
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

            val updated = measurementService.updateMeasurement(
                measurementId = id,
                request = request
            )

            call.respondOk {
                updated
            }
        }

        delete("/range") {
            val (from, to) = call.parseDateRange()

            measurementService.deleteMeasurementsByRange(
                from = from,
                to = to
            )

            call.respondOk {
                mapOf("deleted" to true)
            }
        }

        delete("/{id}") {
            val id = call.requireMeasurementId()

            measurementService.deleteMeasurement(
                measurementId = id
            )

            call.respondOk {
                mapOf("deleted" to true)
            }
        }

        delete {
            val deletedCount = measurementService.deleteAllMeasurements()

            call.respondOk {
                mapOf("deletedCount" to deletedCount)
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