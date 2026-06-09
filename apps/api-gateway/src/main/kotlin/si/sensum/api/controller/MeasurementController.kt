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
import si.sensum.api.domain.requireAdminUser

internal fun Route.measurementController(
    measurementService: MeasurementService
) {
    /**
     * GM import routes.
     *
     * Public API naj uporablja GM ime, ker desktop-demo kliče Eltratec GM integracijo.
     * SWS ostane skrit znotraj eltratec-gm servisa in libs:sws-client.
     *
     * Desktop-demo:
     * POST /api/v1/gm/measurements/refresh
     */
    route("/gm/measurements") {
        post("/refresh") {
            call.requireAdminUser() ?: return@post
            val request = call.receive<RefreshMeasurementsCommand>()

            call.respondCreated {
                measurementService.refreshMeasurements(request)
            }
        }
    }

    /**
     * Normal backend measurement CRUD routes.
     *
     * To pusti za delo s podatki, ki so že v naši bazi.
     */
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
            call.requireAdminUser() ?: return@post
            val request = call.receive<MeasurementDto>()

            call.respondCreated {
                measurementService.createMeasurement(request)
            }
        }

        post("/batch") {
            call.requireAdminUser() ?: return@post
            val request = call.receive<CreateMeasurementsBatchCommand>()

            call.respondCreated {
                measurementService.createMeasurementsBatch(request)
            }
        }

        /**
         * Legacy route.
         *
         * POST /api/v1/gm/measurements/refresh
         */
        post("/refresh") {
            call.requireAdminUser() ?: return@post
            val request = call.receive<RefreshMeasurementsCommand>()

            call.respondCreated {
                measurementService.refreshMeasurements(request)
            }
        }

        post("/regenerate") {
            call.requireAdminUser() ?: return@post
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
            call.requireAdminUser() ?: return@put
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
            call.requireAdminUser() ?: return@delete
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
            call.requireAdminUser() ?: return@delete
            val id = call.requireLongPathParameter("id")

            call.respondOk {
                measurementService.deleteMeasurement(id)
                mapOf("deleted" to true)
            }
        }

        delete {
            call.requireAdminUser() ?: return@delete
            call.respondOk {
                measurementService.deleteAllMeasurements()
                mapOf("deletedAll" to true)
            }
        }
    }
}