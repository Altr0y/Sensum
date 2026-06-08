package si.sensum.api.controller

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.api.service.StatsService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireStringQueryParameter

/**
 * Plain JSON read endpoints consumed by Grafana's "JSON API" datasource
 * (marcusolsson-json-datasource), which maps response fields via JSONPath.
 * Grafana never talks to Postgres directly — it goes through this proxy to backend-core.
 */
internal fun Route.statsController(
    statsService: StatsService
) {
    route("/stats") {
        get("/overview") {
            call.respondOk {
                statsService.getOverview()
            }
        }

        get("/measurements/range") {
            val from = call.requireStringQueryParameter("from")
            val to = call.requireStringQueryParameter("to")

            call.respondOk {
                statsService.getMeasurementsInRange(from = from, to = to)
            }
        }
    }
}
