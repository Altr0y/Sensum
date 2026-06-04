package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.backend.service.StationService
import si.sensum.shared.ktor.validation.requireIntPathParameter

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

    route("/api/v1/municipalities/{municipalityId}/stations") {
        get {
            val municipalityId = call.requireIntPathParameter("municipalityId")

            val stations = stationService.getStationsByMunicipality(municipalityId)

            call.respond(
                HttpStatusCode.OK,
                stations
            )
        }
    }
}