package si.sensum.backend.gm

import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.auth.jwt.JwtUser
import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.api.LoginRequest
import si.sensum.shared.models.api.LoginResponse
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest

class GmClient(
    private val serviceHttpClient: ServiceHttpClient,
    private val serviceJwtTokenService: JwtTokenService
) {
    suspend fun login(username: String, password: String): LoginResponse {
        val serviceToken = serviceJwtTokenService.generateToken(
            JwtUser(
                username = "backend-core",
                role = "SERVICE",
                serviceName = "backend-core"
            )
        )

        return serviceHttpClient.post(
            path = "/api/v1/gm/auth/login",
            body = LoginRequest(username, password),
            bearerToken = serviceToken
        )
    }

    suspend fun getMeasurements(
        gmSessionToken: String,
        request: MeasurementsByStationChannelPairsRequest
    ): List<MeasurementDto> {
        return serviceHttpClient.post(
            path = "/api/v1/gm/measurements/by-station-channel-pairs",
            body = request,
            bearerToken = gmSessionToken
        )
    }
}