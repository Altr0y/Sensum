package si.sensum.api.backend

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsResponse

class BackendClient(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {
    suspend fun getMeasurementsJson(): String {
        val response = httpClient.get("$baseUrl/api/v1/measurements")

        if (!response.status.isSuccess()) {
            throw BackendHttpException(
                statusCode = response.status.value,
                responseBody = response.bodyAsText()
            )
        }

        return response.bodyAsText()
    }

    suspend fun createMeasurement(request: MeasurementDto): MeasurementDto {
        val response = httpClient.post("$baseUrl/api/v1/measurements") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw BackendHttpException(
                    statusCode = response.status.value,
            responseBody = response.bodyAsText()
            )
        }

        return response.body()
    }

    suspend fun updateMeasurement(id: Long, request: MeasurementDto): MeasurementDto {
        val response = httpClient.put("$baseUrl/api/v1/measurements/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw BackendHttpException(
                statusCode = response.status.value,
                responseBody = response.bodyAsText()
            )
        }

        return response.body()
    }

    suspend fun deleteMeasurement(id: Long) {
        val response = httpClient.delete("$baseUrl/api/v1/measurements/$id")

        if (!response.status.isSuccess()) {
            throw BackendHttpException(
                statusCode = response.status.value,
                responseBody = response.bodyAsText()
            )
        }
    }

    suspend fun deleteAllMeasurements() {
        val response = httpClient.delete("$baseUrl/api/v1/measurements")

        if (!response.status.isSuccess()) {
            throw BackendHttpException(
                statusCode = response.status.value,
                responseBody = response.bodyAsText()
            )
        }
    }

    suspend fun refreshMeasurements(
        request: MeasurementsByStationChannelPairsRequest
    ): MeasurementsByStationChannelPairsResponse {
        val response = httpClient.post("$baseUrl/api/v1/measurements/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw BackendHttpException(
                statusCode = response.status.value,
                responseBody = response.bodyAsText()
            )
        }

        return response.body()
    }
}