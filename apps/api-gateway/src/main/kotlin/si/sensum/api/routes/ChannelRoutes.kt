package si.sensum.api.routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.api.backend.BackendChannelClient
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.ktor.validation.requireLongPathParameter

internal fun Route.channelRoutes(
    backendChannels: BackendChannelClient
) {
    route("/channels") {
        get {
            call.respondOk {
                backendChannels.getChannels()
            }
        }

        get("/{channelId}") {
            val channelId = call.requireIntPathParameter("channelId")

            call.respondOk {
                backendChannels.getChannelById(channelId)
            }
        }
    }

    route("/stations/{stationId}/channels") {
        get {
            val stationId = call.requireLongPathParameter("stationId")

            call.respondOk {
                backendChannels.getChannelsByStation(stationId)
            }
        }
    }
}