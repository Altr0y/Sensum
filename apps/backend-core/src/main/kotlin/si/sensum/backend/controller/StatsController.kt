package si.sensum.backend.controller

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.backend.service.StatsService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireLongPathParameter

fun Route.configureStatsRoutes(
    statsService: StatsService
) {
    route("/api/v1/stats") {
        get("/overview") {
            call.respondOk {
                statsService.overview()
            }
        }

        get("/stations") {
            call.respondOk {
                statsService.getStations()
            }
        }

        get("/stations/{stationId}/timerange") {
            val stationId = call.requireLongPathParameter("stationId")
            call.respondOk {
                statsService.stationTimeRange(stationId)
            }
        }

        get("/stations/{stationId}/channels") {
            val stationId = call.requireLongPathParameter("stationId")
            call.respondOk {
                statsService.getChannelsForStation(stationId)
            }
        }

        get("/measurements/range") {
            val (from, to) = call.requireDateRangeQuery()
            val stationId = call.parameters["stationId"]?.toLongOrNull()
            val channelId = call.parameters["channelId"]?.toLongOrNull()
            call.respondOk {
                statsService.measurementsInRange(
                    from = from,
                    to = to,
                    stationId = stationId,
                    channelId = channelId
                )
            }
        }
    }
}