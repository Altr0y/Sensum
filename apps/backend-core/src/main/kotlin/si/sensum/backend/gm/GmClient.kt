package si.sensum.backend.gm

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.auth.jwt.JwtUser
import si.sensum.shared.models.api.LoginRequest
import si.sensum.shared.models.api.LoginResponse
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest

class GmClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
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

        val response = httpClient.post("$baseUrl/api/v1/gm/auth/login") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $serviceToken")
            setBody(LoginRequest(username, password))
        }

        if (!response.status.isSuccess()) {
            error("GM login failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body()
    }

    suspend fun getMeasurements(
        gmSessionToken: String,
        request: MeasurementsByStationChannelPairsRequest
    ): List<MeasurementDto> {
        val response = httpClient.post("$baseUrl/api/v1/gm/measurements/by-station-channel-pairs") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $gmSessionToken")
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            error("GM measurements failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body()
    }
}