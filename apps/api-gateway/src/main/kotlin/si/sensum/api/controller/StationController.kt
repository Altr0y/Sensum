package si.sensum.api.controller

import io.ktor.server.routing.*
import si.sensum.api.domain.requireAuthenticatedUser
import si.sensum.api.service.StationService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireLongPathParameter

internal fun Route.stationController(
    stationService: StationService
) {
    route("/stations") {
        get {
            call.respondOk {
                stationService.getStations()
            }
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