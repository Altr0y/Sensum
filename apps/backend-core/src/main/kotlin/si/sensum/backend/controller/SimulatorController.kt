package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import si.sensum.backend.service.SimulatorService
import si.sensum.shared.models.simulator.SimulateRequestDto

fun Route.configureSimulatorRoutes(
    simulatorService: SimulatorService
) {
    route("/api/v1/simulator") {
        post("/generate") {
            val dto = call.receive<SimulateRequestDto>()
            val result = simulatorService.simulate(dto)
            call.respond(HttpStatusCode.OK, result)
        }
        post("/generate-and-save") {
            val dto = call.receive<SimulateRequestDto>()
            val customerId = 1 // TODO: iz JWT tokena
            val result = simulatorService.simulateAndSave(dto, customerId)
            call.respond(HttpStatusCode.OK, result)
        }
    }
}