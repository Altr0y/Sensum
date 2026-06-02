package si.sensum.demo.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import si.sensum.demo.model.MeasurementUi
import si.sensum.shared.models.auth.LoginCommand
import si.sensum.shared.models.auth.LoginResult
import si.sensum.shared.models.measurements.CreateMeasurementsBatchCommand
import si.sensum.shared.models.measurements.CreateMeasurementsBatchResult
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand
import si.sensum.shared.models.measurements.RefreshMeasurementsResult
import si.sensum.shared.models.measurements.StationChannelPairDto

class SensumApiClient(
    private val baseUrl: String = "http://localhost:8080"
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        expectSuccess = false
    }
    private val session = ApiSession()
    private fun requireToken(): String {
        return session.requireToken()
    }

    suspend fun login(
        username: String,
        password: String
    ): LoginResult {
        val response = client.post("$baseUrl/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginCommand(username = username, password = password))
        }

        if (!response.status.isSuccess()) {
            error("Login failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        val loginResult = response.body<LoginResult>()
        session.saveToken(loginResult.token)

        return loginResult
    }

    suspend fun refreshMeasurements(
        stationChannelPairs: List<StationChannelPairDto>,
        datetimeFrom: String,
        datetimeTo: String
    ): RefreshMeasurementsResult {
        val response = client.post("$baseUrl/api/v1/measurements/refresh") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
            setBody(
                RefreshMeasurementsCommand(
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

    suspend fun getMeasurements(): List<MeasurementUi> {
        val response = client.get("$baseUrl/api/v1/measurements") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
        }

        if (!response.status.isSuccess()) {
            error("Get measurements failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response
            .body<List<MeasurementDto>>()
            .map { it.toUi() }
    }

    suspend fun createMeasurement(measurement: MeasurementUi): MeasurementUi {
        val response = client.post("$baseUrl/api/v1/measurements") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
            setBody(measurement.toDto())
        }

        if (!response.status.isSuccess()) {
            error("Create failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body<MeasurementDto>().toUi()
    }

    suspend fun createMeasurements(measurements: List<MeasurementUi>): Int {
        if (measurements.isEmpty()) {
            return 0
        }

        val response = client.post("$baseUrl/api/v1/measurements/batch") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
            setBody(
                CreateMeasurementsBatchCommand(
                    measurements = measurements.map { it.toDto() }
                )
            )
        }

        if (!response.status.isSuccess()) {
            error("Create batch failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body<CreateMeasurementsBatchResult>().insertedCount
    }

    suspend fun updateMeasurement(measurement: MeasurementUi): MeasurementUi {
        val id = measurement.id ?: error("Cannot update measurement without id")

        val response = client.put("$baseUrl/api/v1/measurements/$id") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
            setBody(measurement.toDto())
        }

        if (!response.status.isSuccess()) {
            error("Update failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body<MeasurementDto>().toUi()
    }

    suspend fun deleteMeasurement(id: Int) {
        val response = client.delete("$baseUrl/api/v1/measurements/$id") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
        }

        if (!response.status.isSuccess()) {
            error("Delete failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }
    }

    suspend fun deleteAllMeasurements() {
        val response = client.delete("$baseUrl/api/v1/measurements") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
        }

        if (!response.status.isSuccess()) {
            error("Delete all failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }
    }

    suspend fun getMeasurementsByRange(
        channelId: Int,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementUi> {
        val response = client.get("$baseUrl/api/v1/measurements/range") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
            parameter("channelId", channelId)
            parameter("from", datetimeFrom)
            parameter("to", datetimeTo)
        }

        if (!response.status.isSuccess()) {
            error("Get measurements by range failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response
            .body<List<MeasurementDto>>()
            .map { it.toUi() }
    }

    suspend fun regenerateMeasurements(
        datetimeFrom: String,
        datetimeTo: String
    ) {
        val response = client.post("$baseUrl/api/v1/measurements/regenerate") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
            parameter("from", datetimeFrom)
            parameter("to", datetimeTo)
        }

        if (!response.status.isSuccess()) {
            error("Regenerate failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }
    }
}