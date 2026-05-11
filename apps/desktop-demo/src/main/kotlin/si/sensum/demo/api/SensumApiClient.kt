package si.sensum.demo.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import si.sensum.demo.model.Measurement
import si.sensum.shared.models.api.LoginRequest
import si.sensum.shared.models.api.LoginResponse
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.RefreshMeasurementsRequest
import si.sensum.shared.models.api.measurements.RefreshMeasurementsResponse
import io.ktor.client.statement.bodyAsText
import si.sensum.shared.models.api.measurements.StationChannelPairDto

class SensumApiClient(
    private val baseUrl: String = "http://localhost:8080"
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        expectSuccess = false
    }

    private var authToken: String? = null

    suspend fun login(): LoginResponse {
        val response = client.post("$baseUrl/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username = "demo", password = "demo"))
        }.body<LoginResponse>()

        authToken = response.token
        return response
    }

    suspend fun refreshMeasurements(
        stationChannelPairs: List<StationChannelPairDto>,
        datetimeFrom: String,
        datetimeTo: String
    ): RefreshMeasurementsResponse {
        val response = client.post("$baseUrl/api/v1/measurements/refresh") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${authToken ?: error("Not logged in")}")
            setBody(
                RefreshMeasurementsRequest(
                    stationChannelPairs = stationChannelPairs,
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            )
        }

        if (!response.status.isSuccess()) {
            error("Refresh failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body()
    }

    suspend fun getMeasurements(): List<Measurement> {
        val dtos = client.get("$baseUrl/api/v1/measurements") {
            header(HttpHeaders.Authorization, "Bearer ${authToken ?: error("Not logged in")}")
        }.body<List<MeasurementDto>>()

        return dtos.map { it.toDesktopMeasurement() }
    }

    suspend fun createMeasurement(measurement: Measurement): Measurement {
        val response = client.post("$baseUrl/api/v1/measurements") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${authToken ?: error("Not logged in")}")
            setBody(measurement.toDto())
        }

        if (!response.status.isSuccess()) {
            error("Create failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body<MeasurementDto>().toDesktopMeasurement()
    }

    suspend fun createMeasurements(measurements: List<Measurement>): Int {
        measurements.forEach {
            createMeasurement(it)
        }

        return measurements.size
    }

    suspend fun updateMeasurement(measurement: Measurement): Measurement {
        val id = measurement.id ?: error("Cannot update measurement without id")

        val response = client.put("$baseUrl/api/v1/measurements/$id") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${authToken ?: error("Not logged in")}")
            setBody(measurement.toDto())
        }

        if (!response.status.isSuccess()) {
            error("Update failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body<MeasurementDto>().toDesktopMeasurement()
    }

    suspend fun deleteMeasurement(id: Int) {
        val response = client.delete("$baseUrl/api/v1/measurements/$id") {
            header(HttpHeaders.Authorization, "Bearer ${authToken ?: error("Not logged in")}")
        }

        if (!response.status.isSuccess()) {
            error("Delete failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }
    }

    suspend fun deleteAllMeasurements() {
        val response = client.delete("$baseUrl/api/v1/measurements") {
            header(HttpHeaders.Authorization, "Bearer ${authToken ?: error("Not logged in")}")
        }

        if (!response.status.isSuccess()) {
            error("Delete all failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }
    }

    private fun Measurement.toDto(): MeasurementDto {
        return MeasurementDto(
            id = id?.toLong(),
            stationId = stationId.toLong(),
            channelId = channelId,
            dateTime = dateTime.atOffset(java.time.ZoneOffset.UTC),
            value = value,
            status = status
        )
    }

    private fun MeasurementDto.toDesktopMeasurement(): Measurement {
        return Measurement(
            id = id?.toInt(),
            stationId = stationId.toInt(),
            stationName = "Station $stationId",
            channelId = channelId,
            channelName = channelName(channelId),
            dateTime = dateTime.toLocalDateTime(),
            value = value,
            status = status
        )
    }

    private fun channelName(channelId: Int): String = when (channelId) {
        127 -> "L8001H - globina voda-radar [m]"
        128 -> "L8001H - višina vode [m]"
        129 -> "L8001H - globina vodnjaka (PPI220) [m]"
        130 -> "PPI220 - Nivo [m]"
        131 -> "PPI220 - Temperatura [°C]"
        132 -> "PPI220 - globina vode-nivo [m]"
        133 -> "L8001H - globina vode-nivo [-]"
        134 -> "L8001H - Nivo [-]"
        135 -> "L8001H - 4 [-]"
        else -> "Channel $channelId"
    }
}