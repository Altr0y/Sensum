package si.sensum.api.controller

import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.api.service.DslService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.dsl.DslProcessRequest

internal fun Route.dslController(dslService: DslService) {
    route("/dsl") {
        post("/process") {
            val request = call.receive<DslProcessRequest>()
            call.respondOk {
                dslService.processSource(request.source)
            }
        }
    }
}
