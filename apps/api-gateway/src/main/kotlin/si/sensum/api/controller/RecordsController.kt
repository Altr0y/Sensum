package si.sensum.api.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.api.service.RecordsService
import si.sensum.shared.ktor.response.respondOk
import java.net.URLEncoder

internal fun Route.recordsController(
    recordsService: RecordsService
) {
    route("/records") {
        get("/stations") {
            call.respondOk {
                recordsService.getStations(call.forwardQueryString())
            }
        }

        get("/channels") {
            call.respondOk {
                recordsService.getChannels(call.forwardQueryString())
            }
        }

        get("/measurements") {
            call.respondOk {
                recordsService.getMeasurements(call.forwardQueryString())
            }
        }
    }
}

private fun ApplicationCall.forwardQueryString(): String {
    val parameters = request.queryParameters

    if (parameters.isEmpty()) {
        return ""
    }

    val encoded = parameters.entries()
        .flatMap { entry ->
            entry.value.map { value ->
                "${entry.key.urlEncode()}=${value.urlEncode()}"
            }
        }
        .joinToString("&")

    return "?$encoded"
}

private fun String.urlEncode(): String {
    return URLEncoder.encode(this, Charsets.UTF_8)
}