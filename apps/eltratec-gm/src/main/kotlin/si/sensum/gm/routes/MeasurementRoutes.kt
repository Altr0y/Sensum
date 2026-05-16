package si.sensum.gm.routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.gm.services.AuthService
import si.sensum.gm.services.MeasurementService
import si.sensum.gm.validation.receiveMeasurementRangeQuery
import si.sensum.gm.validation.receiveStationChannelPairsMeasurementRequest
import si.sensum.gm.validation.requireIntPathParameter
import si.sensum.gm.validation.requireLongPathParameter

internal fun Route.measurementRoutes(
    authService: AuthService,
    measurementService: MeasurementService
) {
    route("/measurements") {
        get {
            call.gmRouteCall(authService) { session ->
                val query = call.receiveMeasurementRangeQuery()

                measurementService.getAllMeasurements(
                    session = session,
                    query = query
                )
            }
        }

        get("/detailed") {
            call.gmRouteCall(authService) { session ->
                val query = call.receiveMeasurementRangeQuery()

                measurementService.getAllMeasurementsDetailed(
                    session = session,
                    query = query
                )
            }
        }

        post("/by-station-channel-pairs") {
            call.gmRouteCall(authService) { session ->
                val request = call.receiveStationChannelPairsMeasurementRequest()

                measurementService.getMeasurementsByStationChannelPairs(
                    session = session,
                    request = request
                )
            }
        }
    }

    route("/stations/{stationId}/measurements") {
        get {
            call.gmRouteCall(authService) { session ->
                val stationId = call.requireLongPathParameter("stationId")
                val query = call.receiveMeasurementRangeQuery()

                measurementService.getStationMeasurements(
                    session = session,
                    stationId = stationId,
                    query = query
                )
            }
        }

        get("/detailed") {
            call.gmRouteCall(authService) { session ->
                val stationId = call.requireLongPathParameter("stationId")
                val query = call.receiveMeasurementRangeQuery()

                measurementService.getStationMeasurementsDetailed(
                    session = session,
                    stationId = stationId,
                    query = query
                )
            }
        }
    }

    route("/stations/{stationId}/channels/{channelId}/measurements") {
        get {
            call.gmRouteCall(authService) { session ->
                val stationId = call.requireLongPathParameter("stationId")
                val channelId = call.requireIntPathParameter("channelId")
                val query = call.receiveMeasurementRangeQuery()

                measurementService.getChannelMeasurements(
                    session = session,
                    stationId = stationId,
                    channelId = channelId,
                    query = query
                )
            }
        }

        get("/detailed") {
            call.gmRouteCall(authService) { session ->
                val stationId = call.requireLongPathParameter("stationId")
                val channelId = call.requireIntPathParameter("channelId")
                val query = call.receiveMeasurementRangeQuery()

                measurementService.getChannelMeasurementsDetailed(
                    session = session,
                    stationId = stationId,
                    channelId = channelId,
                    query = query
                )
            }
        }
    }
}