package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.simulator.SimulateRequestDto
import si.sensum.shared.models.simulator.SimulatedStationDto

internal class BackendSimulatorClient(
    private val backend: ServiceHttpClient
) {
    suspend fun generate(
        request: SimulateRequestDto
    ): List<SimulatedStationDto> {
        return backend.post(
            path = "/api/v1/simulator/generate",
            body = request
        )
    }

    suspend fun generateAndSave(
        request: SimulateRequestDto
    ): List<SimulatedStationDto> {
        return backend.post(
            path = "/api/v1/simulator/generate-and-save",
            body = request
        )
    }
}