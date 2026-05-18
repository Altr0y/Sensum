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

            val fromStr = call.request.queryParameters["from"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'from' parameter")

            val toStr = call.request.queryParameters["to"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'to' parameter")

            val from = runCatching { LocalDateTime.parse(fromStr) }.getOrElse {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid 'from' format, use ISO-8601 e.g. 2026-01-01T00:00:00")
            }

            val to = runCatching { LocalDateTime.parse(toStr) }.getOrElse {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid 'to' format, use ISO-8601 e.g. 2026-01-31T23:59:00")
            }

            val measurements = service.getMeasurements(channelId, from, to)
            call.respond(measurements)
        }
    }
}