package si.sensum.api.service

import si.sensum.api.client.BackendSimulatorClient
import si.sensum.shared.models.simulator.SimulateRequestDto
import si.sensum.shared.models.simulator.SimulatedStationDto

internal class SimulatorService(
    private val simulatorClient: BackendSimulatorClient
) {
    suspend fun generate(
        request: SimulateRequestDto
    ): List<SimulatedStationDto> = ServiceLogger.suspendCall(
        service = "simulator",
        operation = "generate",
        details = "mode=${request.mode} count=${request.count}"
    ) {
        require(request.mode.isNotBlank()) {
            "Simulator mode must not be blank"
        }

        simulatorClient.generate(request)
    }
}