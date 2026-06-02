package si.sensum.gm.routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.gm.services.AuthService
import si.sensum.gm.services.StationService

internal fun Route.stationRoutes(
    authService: AuthService,
    stationService: StationService
) {
    route("/stations") {
        get {
            call.gmRouteCall(authService) { session ->
                stationService.getStations(session)
            }
        }
    }
}