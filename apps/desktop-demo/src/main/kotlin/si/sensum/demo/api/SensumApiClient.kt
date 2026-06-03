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
import si.sensum.shared.models.measurements.*
import si.sensum.shared.models.records.ChannelRecordDto
import si.sensum.shared.models.records.MeasurementRecordDto
import si.sensum.shared.models.records.RecordsPageDto
import si.sensum.shared.models.records.StationRecordDto
import si.sensum.demo.screens.records.RecordsFilter
import si.sensum.demo.screens.records.RecordsSortDirection

class SensumApiClient(
    private val baseUrl: String = System.getenv("SENSUM_API_BASE_URL")
        ?: "http://localhost:3001"
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

    suspend fun getStationRecords(
        page: Int,
        pageSize: Int,
        sortBy: String,
        sortDirection: RecordsSortDirection,
        filters: List<RecordsFilter>
    ): RecordsPageDto<StationRecordDto> {
        return getRecordsPage(
            path = "/api/v1/records/stations",
            page = page,
            pageSize = pageSize,
            sortBy = sortBy,
            sortDirection = sortDirection,
            filters = filters
        )
    }

    suspend fun getChannelRecords(
        page: Int,
        pageSize: Int,
        sortBy: String,
        sortDirection: RecordsSortDirection,
        filters: List<RecordsFilter>
    ): RecordsPageDto<ChannelRecordDto> {
        return getRecordsPage(
            path = "/api/v1/records/channels",
            page = page,
            pageSize = pageSize,
            sortBy = sortBy,
            sortDirection = sortDirection,
            filters = filters
        )
    }

    suspend fun getMeasurementRecords(
        page: Int,
        pageSize: Int,
        sortBy: String,
        sortDirection: RecordsSortDirection,
        filters: List<RecordsFilter>
    ): RecordsPageDto<MeasurementRecordDto> {
        return getRecordsPage(
            path = "/api/v1/records/measurements",
            page = page,
            pageSize = pageSize,
            sortBy = sortBy,
            sortDirection = sortDirection,
            filters = filters
        )
    }

    private suspend inline fun <reified T> getRecordsPage(
        path: String,
        page: Int,
        pageSize: Int,
        sortBy: String,
        sortDirection: RecordsSortDirection,
        filters: List<RecordsFilter>
    ): RecordsPageDto<T> {
        val response = client.get("$baseUrl$path") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")

            parameter("page", page)
            parameter("pageSize", pageSize)
            parameter("sortBy", sortBy)
            parameter("sortDirection", sortDirection.apiValue)

            filters.forEach { filter ->
                parameter(
                    key = "filter",
                    value = "${filter.field}:${filter.operator.apiValue}:${filter.value}"
                )
            }
        }

        if (!response.status.isSuccess()) {
            error("Get records failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body()
    }


    suspend fun logout() {
        val currentToken = runCatching { requireToken() }.getOrNull()

        if (currentToken != null) {
            val response = client.post("$baseUrl/api/v1/auth/logout") {
                header(HttpHeaders.Authorization, "Bearer $currentToken")
            }

            if (!response.status.isSuccess()) {
                session.clear()
                error("Logout failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
            }
        }

        session.clear()
    }

    fun clearSession() {
        session.clear()
    }

}
