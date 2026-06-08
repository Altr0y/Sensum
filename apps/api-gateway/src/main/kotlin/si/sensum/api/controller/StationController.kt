package si.sensum.api.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import si.sensum.api.domain.requireAuthenticatedUser
import si.sensum.api.service.StationService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireLongPathParameter
import si.sensum.shared.models.stations.CreateStationCommand

internal fun Route.stationController(
    stationService: StationService
) {
    route("/stations") {
        get {
            call.respondOk {
                stationService.getStations()
            }
        }

        post {
            val principal = call.requireAuthenticatedUser() ?: return@post
            val command = call.receive<CreateStationCommand>()
            val created = stationService.createStation(command.copy(customerId = principal.customerId))
            call.respond(HttpStatusCode.Created, created)
        }

        get("/geojson") {
            call.respondOk {
                stationService.getStationsGeoJson()
            }
        }

        get("/me") {
            val principal = call.requireAuthenticatedUser() ?: return@get

            call.respondOk {
                stationService.getStationsByCustomer(
                    customerId = principal.customerId
                )
            }
        }

        get("/{stationId}") {
            val stationId = call.requireLongPathParameter("stationId")

            call.respondOk {
                stationService.getStationById(stationId)
            }
        }
    }
}