package si.sensum.api.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import si.sensum.api.backend.BackendMeasurementClient
import si.sensum.shared.ktor.response.respondCreated
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireLongPathParameter
import si.sensum.shared.models.measurements.CreateMeasurementsBatchCommand
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand

internal fun Route.measurementRoutes(
    backendMeasurements: BackendMeasurementClient
) {
    route("/measurements") {

        // Vrne vse meritve iz backend-core.
        get {
            call.respondOk {
                backendMeasurements.getMeasurements()
            }
        }

        // Vrne meritve za izbran kanal in časovni interval.
        get("/range") {
            val channelId = call.requireIntQueryParameter("channelId")
            val datetimeFrom = call.requireStringQueryParameter("from")
            val datetimeTo = call.requireStringQueryParameter("to")

            call.respondOk {
                backendMeasurements.getMeasurementsByRange(
                    channelId = channelId,
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            }
        }

        // Vrne eno meritev po ID-ju.
        get("/{id}") {
            val id = call.requireLongPathParameter("id")

            call.respondOk {
                backendMeasurements.getMeasurementById(id)
            }
        }

        // Ustvari novo meritev v backend-core.
        post {
            val request = call.receive<MeasurementDto>()

            call.respondCreated {
                backendMeasurements.createMeasurement(request)
            }
        }

        // Ustvari več meritev z enim HTTP klicem.
        post("/batch") {
            val request = call.receive<CreateMeasurementsBatchCommand>()

            if (request.measurements.isEmpty()) {
                throw IllegalArgumentException("Measurement batch must not be empty")
            }

            call.respondCreated {
                backendMeasurements.createMeasurementsBatch(request)
            }
        }

        // Osveži realne meritve iz GM/SWS in jih shrani v bazo.
        post("/refresh") {
            val request = call.receive<RefreshMeasurementsCommand>()

            call.respondCreated {
                backendMeasurements.refreshMeasurements(request)
            }
        }

        // Regenerira simulirane meritve za izbran časovni interval.
        post("/regenerate") {
            val datetimeFrom = call.requireStringQueryParameter("from")
            val datetimeTo = call.requireStringQueryParameter("to")

            call.respondOk {
                backendMeasurements.regenerateMeasurements(
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            }
        }

        // Posodobi obstoječo meritev po ID-ju.
        put("/{id}") {
            val id = call.requireLongPathParameter("id")
            val request = call.receive<MeasurementDto>()

            call.respondOk {
                backendMeasurements.updateMeasurement(id, request)
            }
        }

        // Izbriše meritve za izbran časovni interval.
        delete("/range") {
            val datetimeFrom = call.requireStringQueryParameter("from")
            val datetimeTo = call.requireStringQueryParameter("to")

            call.respondOk {
                backendMeasurements.deleteMeasurementsByRange(
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            }
        }

        // Izbriše eno meritev po ID-ju.
        delete("/{id}") {
            val id = call.requireLongPathParameter("id")

            call.respondOk {
                backendMeasurements.deleteMeasurement(id)
                mapOf("deleted" to true)
            }
        }

        // Izbriše vse meritve iz baze.
        delete {
            call.respondOk {
                backendMeasurements.deleteAllMeasurements()
                mapOf("deletedAll" to true)
            }
        }
    }
}

private fun ApplicationCall.requireStringQueryParameter(name: String): String {
    return request.queryParameters[name]
        ?: throw IllegalArgumentException("Missing required query parameter: $name")
}

private fun ApplicationCall.requireIntQueryParameter(name: String): Int {
    val value = requireStringQueryParameter(name)

    return value.toIntOrNull()
        ?: throw IllegalArgumentException("Invalid query parameter '$name': expected integer")
}