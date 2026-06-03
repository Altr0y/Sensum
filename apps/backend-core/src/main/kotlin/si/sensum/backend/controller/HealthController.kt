package si.sensum.backend.controller

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import si.sensum.shared.models.common.HealthDto

fun Route.configureHealthRoutes() {
    get("/health") {
        call.respond(
            HealthDto(
                status = "ok",
                service = "backend-core"
            )
        )
    }
}