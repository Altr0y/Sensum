package si.sensum.gm.routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.gm.services.AuthService
import si.sensum.gm.services.ChannelService
import si.sensum.gm.validation.requireLongPathParameter

internal fun Route.channelRoutes(
    authService: AuthService,
    channelService: ChannelService
) {
    route("/stations/{stationId}/channels") {
        // SWS: GetChannels
        // REST field: stationId
        // SWS field: StationID
        get {
            call.gmRouteCall(authService) { session ->
                val stationId = call.requireLongPathParameter("stationId")

                channelService.getChannels(
                    session = session,
                    stationId = stationId
                )
            }
        }
    }
}