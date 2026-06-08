package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.backend.service.StationService
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.models.stations.CreateStationCommand

fun Route.configureStationRoutes(
    stationService: StationService
) {
    route("/api/v1/stations") {
        get {
            val stations = stationService.getAllStations()

            call.respond(
                HttpStatusCode.OK,
                stations
            )
        }

        post {
            val command = call.receive<CreateStationCommand>()
            val created = stationService.createStation(command)
            call.respond(HttpStatusCode.Created, created)
        }

        get("/{stationId}") {
            val stationId = call.parameters["stationId"]?.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid station id")

            val station = stationService.getStation(stationId)

            call.respond(
                HttpStatusCode.OK,
                station
            )
        }
    }

    route("/api/v1/customers/{customerId}/stations") {
        get {
            val customerId = call.requireIntPathParameter("customerId")

            val stations = stationService.getStationsByCustomer(customerId)

            call.respond(
                HttpStatusCode.OK,
                stations
            )
        }
    }
}