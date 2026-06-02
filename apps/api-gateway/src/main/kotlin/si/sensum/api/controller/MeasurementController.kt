package si.sensum.api.controller

import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import si.sensum.api.service.MeasurementService
import si.sensum.shared.ktor.response.respondCreated
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireIntQueryParameter
import si.sensum.shared.ktor.validation.requireLongPathParameter
import si.sensum.shared.ktor.validation.requireStringQueryParameter
import si.sensum.shared.models.measurements.CreateMeasurementsBatchCommand
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand

internal fun Route.measurementController(
    measurementService: MeasurementService
) {
    route("/measurements") {

        get {
            call.respondOk {
                measurementService.getMeasurements()
            }
        }

        get("/range") {
            val channelId = call.requireIntQueryParameter("channelId")
            val datetimeFrom = call.requireStringQueryParameter("from")
            val datetimeTo = call.requireStringQueryParameter("to")

            call.respondOk {
                measurementService.getMeasurementsByRange(
                    channelId = channelId,
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            }
        }

        get("/{id}") {
            val id = call.requireLongPathParameter("id")

            call.respondOk {
                measurementService.getMeasurementById(id)
            }
        }

        post {
            val request = call.receive<MeasurementDto>()

            call.respondCreated {
                measurementService.createMeasurement(request)
            }
        }

        post("/batch") {
            val request = call.receive<CreateMeasurementsBatchCommand>()

            call.respondCreated {
                measurementService.createMeasurementsBatch(request)
            }
        }

        post("/refresh") {
            val request = call.receive<RefreshMeasurementsCommand>()

            call.respondCreated {
                measurementService.refreshMeasurements(request)
            }
        }

        post("/regenerate") {
            val datetimeFrom = call.requireStringQueryParameter("from")
            val datetimeTo = call.requireStringQueryParameter("to")

            call.respondOk {
                measurementService.regenerateMeasurements(
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            }
        }

        put("/{id}") {
            val id = call.requireLongPathParameter("id")
            val request = call.receive<MeasurementDto>()

            call.respondOk {
                measurementService.updateMeasurement(
                    id = id,
                    request = request
                )
            }
        }

        delete("/range") {
            val datetimeFrom = call.requireStringQueryParameter("from")
            val datetimeTo = call.requireStringQueryParameter("to")

            call.respondOk {
                measurementService.deleteMeasurementsByRange(
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            }
        }

        delete("/{id}") {
            val id = call.requireLongPathParameter("id")

            call.respondOk {
                measurementService.deleteMeasurement(id)
                mapOf("deleted" to true)
            }
        }

        delete {
            call.respondOk {
                measurementService.deleteAllMeasurements()
                mapOf("deletedAll" to true)
            }
        }
    }
}