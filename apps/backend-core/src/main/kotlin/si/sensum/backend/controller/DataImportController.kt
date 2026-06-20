package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.backend.service.DataImportService
import si.sensum.shared.models.data.DataImportCommand
import si.sensum.shared.models.data.DslImportCommand
import si.sensum.shared.models.data.SwsImportCommand

fun Route.configureDataImportRoutes(
    service: DataImportService
) {
    route("/api/v1/data/import") {
        post("/manual") {
            val command = call.receive<DataImportCommand>()

            call.respond(
                status = HttpStatusCode.Created,
                message = service.importManual(command)
            )
        }

        post("/simulation") {
            val command = call.receive<DataImportCommand>()

            call.respond(
                status = HttpStatusCode.Created,
                message = service.importSimulation(command)
            )
        }

        post("/dsl") {
            val command = call.receive<DslImportCommand>()

            call.respond(
                status = HttpStatusCode.Created,
                message = service.importDsl(command)
            )
        }

        post("/sws/stations") {
            val command = call.receive<SwsImportCommand>()

            call.respond(
                status = HttpStatusCode.Created,
                message = service.importSwsStations(command)
            )
        }

        post("/sws/channels") {
            val command = call.receive<SwsImportCommand>()

            call.respond(
                status = HttpStatusCode.Created,
                message = service.importSwsChannels(command)
            )
        }

        post("/sws/measurements") {
            val command = call.receive<SwsImportCommand>()

            call.respond(
                status = HttpStatusCode.Created,
                message = service.importSwsMeasurements(command)
            )
        }

        post("/sws/modbus-measurements") {
            val command = call.receive<SwsImportCommand>()
            call.respond(
                status = HttpStatusCode.Created,
                message = service.importSwsModbusMeasurements(command)
            )
        }
    }
}