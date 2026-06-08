package si.sensum.backend.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.datetime.LocalDateTime
import si.sensum.backend.service.StatsService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.datetime.ApiDateTime

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
            val (from, to) = call.parseDateRange()

            call.respondOk {
                statsService.measurementsInRange(from = from, to = to)
            }
        }
    }
}

private fun ApplicationCall.parseDateRange(): Pair<LocalDateTime, LocalDateTime> {
    val fromRaw = request.queryParameters["from"]
        ?: throw IllegalArgumentException("Missing required query parameter: from")

    val toRaw = request.queryParameters["to"]
        ?: throw IllegalArgumentException("Missing required query parameter: to")

    val from = LocalDateTime.parse(
        ApiDateTime.requireNormalizedLocal(
            fieldName = "from",
            value = fromRaw
        )
    )

    val to = LocalDateTime.parse(
        ApiDateTime.requireNormalizedLocal(
            fieldName = "to",
            value = toRaw
        )
    )

    return from to to
}
