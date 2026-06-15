package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.backend.service.StationService
import si.sensum.shared.ktor.validation.requireIntPathParameter
import si.sensum.shared.ktor.validation.requireLongPathParameter
import si.sensum.shared.models.stations.CreateStationCommand
import io.ktor.server.routing.patch
import si.sensum.shared.models.stations.UpdateStationCommand

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

        delete("/{stationId}") {
            val stationId = call.requireLongPathParameter("stationId")
            val deleted = stationService.deleteStation(stationId)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        patch("/{stationId}") {
            val stationId = call.requireLongPathParameter("stationId")
            val command = call.receive<UpdateStationCommand>()
            val updated = stationService.updateStation(stationId, command)
            call.respond(HttpStatusCode.OK, updated)
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

        get("/{stationId}") {
            val customerId = call.requireIntPathParameter("customerId")
            val stationId = call.requireLongPathParameter("stationId")

            call.respond(
                HttpStatusCode.OK,
                stationService.getStationByCustomer(
                    customerId = customerId,
                    stationId = stationId
                )
            )
        }
    }

    route("/api/v1/customers/{customerId}/users/{userId}/stations") {
        get {
            val customerId = call.requireIntPathParameter("customerId")
            val userId = call.requireIntPathParameter("userId")

            call.respond(
                HttpStatusCode.OK,
                stationService.getStationsByUser(
                    customerId = customerId,
                    userId = userId
                )
            )
        }

        get("/{stationId}") {
            val customerId = call.requireIntPathParameter("customerId")
            val userId = call.requireIntPathParameter("userId")
            val stationId = call.requireLongPathParameter("stationId")

            call.respond(
                HttpStatusCode.OK,
                stationService.getStationByUser(
                    customerId = customerId,
                    userId = userId,
                    stationId = stationId
                )
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