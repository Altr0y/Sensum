package si.sensum.api.controller

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.api.service.ChannelService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.ktor.validation.requireLongPathParameter
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import si.sensum.shared.models.channels.CreateChannelsRequest

internal fun Route.channelController(
    channelService: ChannelService
) {
    route("/channels") {
        get {
            call.respondOk {
                channelService.getChannels()
            }
        }

        get("/{channelId}") {
            val channelId = call.requireIntPathParameter("channelId")

            call.respondOk {
                channelService.getChannelById(channelId)
            }
        }
    }

    route("/stations/{stationId}/channels") {
        get {
            val stationId = call.requireLongPathParameter("stationId")
            call.respondOk {
                channelService.getChannelsByStation(stationId)
            }
        }
        post {
            val stationId = call.requireLongPathParameter("stationId")
            val request = call.receive<CreateChannelsRequest>()
            call.respondOk {
                channelService.createChannels(stationId, request)
            }
        }
    }
}