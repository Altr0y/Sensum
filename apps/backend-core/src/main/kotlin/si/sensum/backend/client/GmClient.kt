package si.sensum.backend.client

import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.auth.jwt.JwtUser
import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.auth.LoginCommand
import si.sensum.shared.models.auth.LoginResult
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand

class GmClient(
    private val serviceHttpClient: ServiceHttpClient,
    private val serviceJwtTokenService: JwtTokenService
) {
    suspend fun login(username: String, password: String): LoginResult {
        val serviceToken = serviceJwtTokenService.generateToken(
            JwtUser(
                username = "backend-core",
                role = "SERVICE",
                serviceName = "backend-core"
            )
        )

        return serviceHttpClient.post(
            path = "/api/v1/gm/auth/login",
            body = LoginCommand(username, password),
            bearerToken = serviceToken
        )
    }

    suspend fun getMeasurements(
        gmSessionToken: String,
        request: RefreshMeasurementsCommand
    ): List<MeasurementDto> {
        return serviceHttpClient.post(
            path = "/api/v1/gm/measurements/by-station-channel-pairs",
            body = request,
            bearerToken = gmSessionToken
        )
    }
}