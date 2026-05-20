package si.sensum.api.routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.api.backend.BackendStationClient
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireLongPathParameter

internal fun Route.stationRoutes(
    backendStations: BackendStationClient
) {
    route("/stations") {
        get {
            call.respondOk {
                backendStations.getStations()
            }
        }

        get("/{stationId}") {
            val stationId = call.requireLongPathParameter("stationId")

            call.respondOk {
                backendStations.getStationById(stationId)
            }
        }
    }
}