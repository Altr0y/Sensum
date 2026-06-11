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
        // SWS: GetAllMeasurements
        // REST query: ?from=2026-01-01T00:00:00&to=2026-01-02T00:00:00
        // SWS fields: datetimeFrom, datetimeTo
        get {
            call.gmRouteCall(authService) { session ->
                val query = call.receiveMeasurementRangeQuery()

                measurementService.getAllMeasurements(
                    session = session,
                    query = query
                )
            }
        }

        // SWS: GetAllMeasurements_Detailed
        get("/detailed") {
            call.gmRouteCall(authService) { session ->
                val query = call.receiveMeasurementRangeQuery()

                measurementService.getAllMeasurementsDetailed(
                    session = session,
                    query = query
                )
            }
        }

        // SWS: GetMeasurementsByStationChannelPairs
        // Body:
        // {
        //   "stationChannelPairs": [
        //     { "stationId": 2241, "channelId": 101 }
        //   ],
        //   "datetimeFrom": "2026-01-01T00:00:00",
        //   "datetimeTo": "2026-01-02T00:00:00"
        // }
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
        // SWS: GetStationMeasurements
        // REST path: stationId
        // REST query: from, to
        // SWS fields: StationID, datetimeFrom, datetimeTo
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

        // SWS: GetStationMeasurements_Detailed
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
        // SWS: GetChannelMeasurements
        // REST path: stationId, channelId
        // REST query: from, to
        // SWS fields: StationID, ChannelID, datetimeFrom, datetimeTo
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

        // SWS: GetChannelMeasurements_Detailed
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