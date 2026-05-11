package si.sensum.api.backend

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.RefreshMeasurementsRequest
import si.sensum.shared.models.api.measurements.RefreshMeasurementsResponse

class BackendClient(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {
    suspend fun getMeasurementsJson(): String {
        val response = httpClient.get("$baseUrl/api/v1/measurements")

        if (!response.status.isSuccess()) {
            throw RuntimeException("Backend GET measurements failed: HTTP ${response.status.value}")
        }

        return response.bodyAsText()
    }

    suspend fun createMeasurement(request: MeasurementDto): MeasurementDto {
        val response = httpClient.post("$baseUrl/api/v1/measurements") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            error("Backend create measurement failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body()
    }

    suspend fun updateMeasurement(id: Long, request: MeasurementDto): MeasurementDto {
        val response = httpClient.put("$baseUrl/api/v1/measurements/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            error("Backend update measurement failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body()
    }

    suspend fun deleteMeasurement(id: Long) {
        val response = httpClient.delete("$baseUrl/api/v1/measurements/$id")

        if (!response.status.isSuccess()) {
            error("Backend delete measurement failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }
    }

    suspend fun deleteAllMeasurements() {
        val response = httpClient.delete("$baseUrl/api/v1/measurements")

        if (!response.status.isSuccess()) {
            error("Backend delete all measurements failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }
    }

    suspend fun refreshMeasurements(
        request: RefreshMeasurementsRequest
    ): RefreshMeasurementsResponse {
        val response = httpClient.post("$baseUrl/api/v1/measurements/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            error("Backend refresh failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body()
    }
}