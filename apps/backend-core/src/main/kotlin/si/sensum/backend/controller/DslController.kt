package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.backend.service.DslService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.common.ApiError
import si.sensum.shared.models.dsl.DslProcessRequest

internal fun Route.configureDslRoutes(dslService: DslService) {
    route("/api/v1/dsl") {
        post("/process") {
            val request = call.receive<DslProcessRequest>()
            val source = request.source.trim()

            if (source.isBlank()) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ApiError(error = "DSL source must not be blank.")
                )
                return@post
            }

            call.respondOk {
                dslService.processSource(source)
            }
        }
    }
}