package si.sensum.api.controller

import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.api.domain.requireAdminUser
import si.sensum.api.service.DataImportService
import si.sensum.shared.ktor.response.respondCreated
import si.sensum.shared.models.data.DataImportCommand
import si.sensum.shared.models.data.DslImportCommand
import si.sensum.shared.models.data.SwsImportCommand

internal fun Route.dataImportController(
    service: DataImportService
) {
    route("/data/import") {
        post("/manual") {
            val principal = call.requireAdminUser() ?: return@post
            val command = call.receive<DataImportCommand>()

            call.respondCreated {
                service.importManual(
                    principal = principal,
                    command = command
                )
            }
        }

        post("/simulation") {
            val principal = call.requireAdminUser() ?: return@post
            val command = call.receive<DataImportCommand>()

            call.respondCreated {
                service.importSimulation(
                    principal = principal,
                    command = command
                )
            }
        }

        post("/dsl") {
            val principal = call.requireAdminUser() ?: return@post
            val command = call.receive<DslImportCommand>()

            call.respondCreated {
                service.importDsl(
                    principal = principal,
                    source = command.source
                )
            }
        }

        post("/sws/stations") {
            val principal = call.requireAdminUser() ?: return@post
            val command = call.receive<SwsImportCommand>()

            call.respondCreated {
                service.importSwsStations(
                    principal = principal,
                    command = command
                )
            }
        }

        post("/sws/channels") {
            val principal = call.requireAdminUser() ?: return@post
            val command = call.receive<SwsImportCommand>()

            call.respondCreated {
                service.importSwsChannels(
                    principal = principal,
                    command = command
                )
            }
        }

        post("/sws/measurements") {
            val principal = call.requireAdminUser() ?: return@post
            val command = call.receive<SwsImportCommand>()

            call.respondCreated {
                service.importSwsMeasurements(
                    principal = principal,
                    command = command
                )
            }
        }
    }
}