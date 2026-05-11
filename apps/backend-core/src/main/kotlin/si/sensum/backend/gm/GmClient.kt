package si.sensum.backend.gm

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import si.sensum.shared.models.api.LoginRequest
import si.sensum.shared.models.api.LoginResponse
import si.sensum.shared.models.api.measurements.MeasurementDto
import io.ktor.client.statement.bodyAsText
import si.sensum.shared.models.api.measurements.RefreshMeasurementsRequest

class GmClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val gmApiAuthToken: String
) {
    suspend fun login(username: String, password: String): LoginResponse {
        return httpClient.post("$baseUrl/api/v1/auth/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Authorization, "Bearer $gmApiAuthToken")
            setBody(LoginRequest(username, password))
        }.body()
    }

    suspend fun getMeasurements(
        gmSessionToken: String,
        request: RefreshMeasurementsRequest
    ): List<MeasurementDto> {
        val response = httpClient.post("$baseUrl/api/v1/measurements/by-station-channel-pairs") {
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