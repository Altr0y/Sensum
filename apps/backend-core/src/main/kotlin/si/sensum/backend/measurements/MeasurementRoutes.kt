package si.sensum.backend.measurements

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime

fun Route.measurementRoutes() {
    val service = MeasurementService()

    route("/api/measurements") {
        get {
            val channelId = call.request.queryParameters["channelId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or invalid channelId")

            val (from, to) = call.parseDateRange()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing 'from'/'to' parameters, use ISO-8601 e.g. 2026-01-01T00:00:00")

            call.respond(service.getMeasurements(channelId, from, to))
        }

        post("/regenerate") {
            val (from, to) = call.parseDateRange()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing 'from'/'to' parameters, use ISO-8601 e.g. 2026-01-01T00:00:00")

            service.regenerateMeasurements(from, to)
            call.respond(HttpStatusCode.OK, "Regenerated measurements from $from to $to")
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.parseDateRange(): Pair<LocalDateTime, LocalDateTime>? {
    val from = runCatching {
        LocalDateTime.parse(request.queryParameters["from"] ?: return null)
    }.getOrElse { return null }

    val to = runCatching {
        LocalDateTime.parse(request.queryParameters["to"] ?: return null)
    }.getOrElse { return null }

    return Pair(from, to)
}