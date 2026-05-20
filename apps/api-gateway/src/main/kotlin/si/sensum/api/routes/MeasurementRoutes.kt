package si.sensum.api.routes

import io.ktor.server.request.*
import io.ktor.server.routing.*
import si.sensum.api.backend.BackendMeasurementClient
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireLongPathParameter
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest

internal fun Route.measurementRoutes(
    backendMeasurements: BackendMeasurementClient
) {
    route("/measurements") {
        get {
            call.respondOk {
                backendMeasurements.getMeasurements()
            }
        }

        get("/{id}") {
            val id = call.requireLongPathParameter("id")

            call.respondOk {
                backendMeasurements.getMeasurementById(id)
            }
        }

        post {
            val request = call.receive<MeasurementDto>()

            call.respondOk {
                backendMeasurements.createMeasurement(request)
            }
        }

        put("/{id}") {
            val id = call.requireLongPathParameter("id")
            val request = call.receive<MeasurementDto>()

            call.respondOk {
                backendMeasurements.updateMeasurement(id, request)
            }
        }

        delete("/{id}") {
            val id = call.requireLongPathParameter("id")

            call.respondOk {
                backendMeasurements.deleteMeasurement(id)
                mapOf("deleted" to true)
            }
        }

        delete {
            call.respondOk {
                backendMeasurements.deleteAllMeasurements()
                mapOf("deletedAll" to true)
            }
        }

        post("/refresh") {
            val request = call.receive<MeasurementsByStationChannelPairsRequest>()

            call.respondOk {
                backendMeasurements.refreshMeasurements(request)
            }
        }
    }
}