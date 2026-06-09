package si.sensum.backend.controller

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.backend.service.StatsService
import si.sensum.shared.ktor.response.respondOk

fun Route.configureStatsRoutes(
    statsService: StatsService
) {
    route("/api/v1/stats") {
        get("/overview") {
            call.respondOk {
                statsService.overview()
            }
        }

        get("/measurements/range") {
            val (from, to) = call.requireDateRangeQuery()

            call.respondOk {
                statsService.measurementsInRange(
                    from = from,
                    to = to
                )
            }
        }
    }
}