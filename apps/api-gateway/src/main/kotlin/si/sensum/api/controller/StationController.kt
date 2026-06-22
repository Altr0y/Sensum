package si.sensum.api.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import si.sensum.api.domain.requireAdminUser
import si.sensum.api.domain.requireAuthenticatedUser
import si.sensum.api.service.StationService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireLongPathParameter
import si.sensum.shared.models.stations.CreateStationCommand
import io.ktor.server.routing.patch
import si.sensum.shared.models.stations.UpdateStationCommand

internal fun Route.stationController(
    stationService: StationService
) {
    route("/stations") {
        get {
            val principal = call.requireAuthenticatedUser() ?: return@get

            call.respondOk {
                stationService.getVisibleStations(principal)
            }
        }

        post {
            val principal = call.requireAdminUser() ?: return@post
            val command = call.receive<CreateStationCommand>()

            val created = stationService.createStation(
                command.copy(customerId = principal.customerId)
            )

            call.respond(HttpStatusCode.Created, created)
        }

        get("/geojson") {
            val principal = call.requireAuthenticatedUser() ?: return@get

            call.respondOk {
                stationService.getVisibleStationsGeoJson(principal)
            }
        }

        get("/me") {
            val principal = call.requireAuthenticatedUser() ?: return@get

            call.respondOk {
                stationService.getVisibleStations(principal)
            }
        }

        get("/{stationId}") {
            val principal = call.requireAuthenticatedUser() ?: return@get
            val stationId = call.requireLongPathParameter("stationId")

            call.respondOk {
                stationService.getVisibleStationById(
                    principal = principal,
                    stationId = stationId
                )
            }
        }
        delete("/{stationId}") {
            val principal = call.requireAdminUser() ?: return@delete
            val stationId = call.requireLongPathParameter("stationId")
            call.respondOk {
                stationService.deleteStation(principal, stationId)
            }
        }

        patch("/{stationId}") {
            val principal = call.requireAdminUser() ?: return@patch
            val stationId = call.requireLongPathParameter("stationId")
            val command = call.receive<UpdateStationCommand>()
            call.respondOk {
                stationService.updateStation(principal, stationId, command)
            }
        }

    }
}