package si.sensum.api.controller

import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.api.service.SimulatorService
import si.sensum.shared.ktor.response.respondOk
import si.sensum.shared.models.simulator.SimulateRequestDto

internal fun Route.simulatorController(
    simulatorService: SimulatorService
) {
    route("/simulator") {
        post("/generate") {
            val request = call.receive<SimulateRequestDto>()

            call.respondOk {
                simulatorService.generate(request)
            }
        }
        post("/generate-and-save") {
            val request = call.receive<SimulateRequestDto>()

            call.respondOk {
                simulatorService.generateAndSave(request)
            }
        }
    }
}