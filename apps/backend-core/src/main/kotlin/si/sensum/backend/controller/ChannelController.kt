package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.backend.service.ChannelService

fun Route.configureChannelRoutes(
    channelService: ChannelService
) {
    route("/api/v1/channels") {
        get {
            val channels = channelService.getAllChannels()

            call.respond(
                HttpStatusCode.OK,
                channels
            )
        }

        get("/{channelId}") {
            val channelId = call.parameters["channelId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid channel id")

            val channel = channelService.getChannel(channelId)

            call.respond(
                HttpStatusCode.OK,
                channel
            )
        }
    }

    route("/api/v1/stations/{stationId}/channels") {
        get {
            val stationId = call.parameters["stationId"]?.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid station id")

            val channels = channelService.getChannelsByStation(stationId)

            call.respond(
                HttpStatusCode.OK,
                channels
            )
        }
    }
}