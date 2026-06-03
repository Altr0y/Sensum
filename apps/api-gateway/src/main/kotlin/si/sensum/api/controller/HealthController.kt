package si.sensum.api.controller

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import si.sensum.shared.models.common.HealthDto

private const val SERVICE_NAME = "api-gateway"

internal fun Route.healthController() {
    get("/health") {
        call.respond(
            HealthDto(
                status = "ok",
                service = SERVICE_NAME
            )
        )
    }
}