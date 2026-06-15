package si.sensum.api.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.api.service.StatsService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.ktor.validation.requireStringQueryParameter
import io.ktor.server.response.respond

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

        get("/stations") {
            call.respondOk {
                statsService.getStations()
            }
        }

        get("/stations/{stationId}/channels") {
            val stationId = call.parameters["stationId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            call.respondOk {
                statsService.getChannelsForStation(stationId)
            }
        }

        get("/stations/{stationId}/timerange") {
            val stationId = call.parameters["stationId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            call.respondOk {
                statsService.getStationTimeRange(stationId)
            }
        }

        get("/measurements/range") {
            val from = call.requireStringQueryParameter("from")
            val to = call.requireStringQueryParameter("to")
            val stationId = call.parameters["stationId"]?.toLongOrNull()
            val channelId = call.parameters["channelId"]?.toLongOrNull()
            call.respondOk {
                statsService.getMeasurementsInRange(from = from, to = to, stationId = stationId, channelId = channelId)
            }
        }
    }
}
