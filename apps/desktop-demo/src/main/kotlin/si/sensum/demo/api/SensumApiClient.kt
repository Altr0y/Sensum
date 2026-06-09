package si.sensum.demo.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.screens.records.RecordsFilter
import si.sensum.demo.screens.records.RecordsSortDirection
import si.sensum.shared.models.auth.LoginCommand
import si.sensum.shared.models.auth.LoginResult
import si.sensum.shared.models.dsl.DslProcessRequest
import si.sensum.shared.models.dsl.DslProcessResult
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.shared.models.measurements.CreateMeasurementsBatchCommand
import si.sensum.shared.models.measurements.CreateMeasurementsBatchResult
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand
import si.sensum.shared.models.measurements.RefreshMeasurementsResult
import si.sensum.shared.models.measurements.StationChannelPairDto
import si.sensum.shared.models.records.ChannelRecordDto
import si.sensum.shared.models.records.MeasurementRecordDto
import si.sensum.shared.models.records.RecordsPageDto
import si.sensum.shared.models.records.StationRecordDto
import si.sensum.shared.models.stations.StationDto
import si.sensum.shared.models.simulator.SimulateRequestDto
import si.sensum.shared.models.simulator.SimulatedStationDto
import java.io.IOException

class SensumApiClient(
    val baseUrl: String = System.getenv("SENSUM_API_BASE_URL")
        ?: "http://localhost:3001"
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }

        expectSuccess = false
    }

    private val session = ApiSession()

    suspend fun login(
        username: String,
        password: String
    ): LoginResult {
        val result = post<LoginResult>(
            path = "/api/v1/auth/login",
            operation = "Login",
            authenticated = false
        ) {
            setBody(LoginCommand(username = username, password = password))
        }

        session.saveToken(result.token)
        return result
    }

    suspend fun refreshMeasurements(
        stationChannelPairs: List<StationChannelPairDto>,
        datetimeFrom: String,
        datetimeTo: String
    ): RefreshMeasurementsResult {
        return post(
            path = "/api/v1/measurements/refresh",
            operation = "Refresh measurements"
        ) {
            setBody(
                RefreshMeasurementsCommand(
                    stationChannelPairs = stationChannelPairs,
                    datetimeFrom = datetimeFrom,
                    datetimeTo = datetimeTo
                )
            )
        }
    }

    suspend fun getMeasurements(): List<MeasurementUi> {
        return get<List<MeasurementDto>>(
            path = "/api/v1/measurements",
            operation = "Get measurements"
        ).map { measurementDto ->
            measurementDto.toUi()
        }
    }

    suspend fun createMeasurement(measurement: MeasurementUi): MeasurementUi {
        return post<MeasurementDto>(
            path = "/api/v1/measurements",
            operation = "Create measurement"
        ) {
            setBody(measurement.toDto())
        }.toUi()
    }

    suspend fun createMeasurements(measurements: List<MeasurementUi>): Int {
        if (measurements.isEmpty()) {
            return 0
        }

        return post<CreateMeasurementsBatchResult>(
            path = "/api/v1/measurements/batch",
            operation = "Create measurements batch"
        ) {
            setBody(
                CreateMeasurementsBatchCommand(
                    measurements = measurements.map { measurement ->
                        measurement.toDto()
                    }
                )
            )
        }.insertedCount
    }

    suspend fun updateMeasurement(measurement: MeasurementUi): MeasurementUi {
        val id = measurement.id
            ?: throw SensumApiException(
                operation = "Update measurement",
                method = "PUT",
                url = fullUrl("/api/v1/measurements/<missing-id>"),
                serviceMessage = "Cannot update measurement without id"
            )

        return put<MeasurementDto>(
            path = "/api/v1/measurements/$id",
            operation = "Update measurement"
        ) {
            setBody(measurement.toDto())
        }.toUi()
    }

    suspend fun deleteMeasurement(id: Int) {
        delete(
            path = "/api/v1/measurements/$id",
            operation = "Delete measurement"
        )
    }

    @Suppress("unused")
    suspend fun deleteAllMeasurements() {
        delete(
            path = "/api/v1/measurements",
            operation = "Delete all measurements"
        )
    }

    suspend fun simulate(
        request: SimulateRequestDto
    ): List<SimulatedStationDto> {
        return post(
            path = "/api/v1/simulator/generate",
            operation = "Generate simulator data"
        ) {
            setBody(request)
        }
    }

    suspend fun processDsl(source: String): DslProcessResult {
        return post(
            path = "/api/v1/dsl/process",
            operation = "Process DSL"
        ) {
            setBody(DslProcessRequest(source = source))
        }
    }

    suspend fun getMeasurementsByRange(
        channelId: Int,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementUi> {
        return get<List<MeasurementDto>>(
            path = "/api/v1/measurements/range",
            operation = "Get measurements by range"
        ) {
            parameter("channelId", channelId)
            parameter("from", datetimeFrom)
            parameter("to", datetimeTo)
        }.map { measurementDto ->
            measurementDto.toUi()
        }
    }

    @Suppress("unused")
    suspend fun regenerateMeasurements(
        datetimeFrom: String,
        datetimeTo: String
    ) {
        post<Map<String, Boolean>>(
            path = "/api/v1/measurements/regenerate",
            operation = "Regenerate measurements"
        ) {
            parameter("from", datetimeFrom)
            parameter("to", datetimeTo)
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

    suspend fun getSwsStations(): List<StationDto> {
        val response: HttpResponse = client.get("$baseUrl/api/v1/gm/stations") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
        }

        if (!response.status.isSuccess()) {
            error("Get SWS stations failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body<List<StationDto>>()
    }

    suspend fun getSwsChannels(
        stationId: Long
    ): List<ChannelDto> {
        val response: HttpResponse = client.get("$baseUrl/api/v1/gm/stations/$stationId/channels") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
        }

        if (!response.status.isSuccess()) {
            error("Get SWS channels failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body<List<ChannelDto>>()
    }

    suspend fun getSwsStationMeasurements(
        stationId: Long,
        datetimeFrom: String,
        datetimeTo: String,
        detailed: Boolean = false
    ): List<MeasurementUi> {
        val suffix = if (detailed) "/detailed" else ""

        val response: HttpResponse = client.get("$baseUrl/api/v1/gm/stations/$stationId/measurements$suffix") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
            parameter("from", datetimeFrom)
            parameter("to", datetimeTo)
        }

        if (!response.status.isSuccess()) {
            error("Get SWS station measurements failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body<List<MeasurementDto>>().map { measurementDto ->
            measurementDto.toUi()
        }
    }

    suspend fun getSwsChannelMeasurements(
        stationId: Long,
        channelId: Int,
        datetimeFrom: String,
        datetimeTo: String,
        detailed: Boolean = false
    ): List<MeasurementUi> {
        val suffix = if (detailed) "/detailed" else ""

        val response: HttpResponse = client.get("$baseUrl/api/v1/gm/stations/$stationId/channels/$channelId/measurements$suffix") {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
            parameter("from", datetimeFrom)
            parameter("to", datetimeTo)
        }

        if (!response.status.isSuccess()) {
            error("Get SWS channel measurements failed: HTTP ${response.status.value}\n${response.bodyAsText()}")
        }

        return response.body<List<MeasurementDto>>().map { measurementDto ->
            measurementDto.toUi()
        }
    }

    suspend fun logout() {
        val currentToken = runCatching {
            requireToken()
        }.getOrNull()

        if (currentToken != null) {
            runCatching {
                post<Map<String, Boolean>>(
                    path = "/api/v1/auth/logout",
                    operation = "Logout",
                    authenticated = false
                ) {
                    header(HttpHeaders.Authorization, "Bearer $currentToken")
                }
            }.onFailure { error ->
                session.clear()
                throw error
            }
        }

        session.clear()
    }

    @Suppress("unused")
    fun clearSession() {
        session.clear()
    }

    private suspend inline fun <reified T> getRecordsPage(
        path: String,
        page: Int,
        pageSize: Int,
        sortBy: String,
        sortDirection: RecordsSortDirection,
        filters: List<RecordsFilter>
    ): RecordsPageDto<T> {
        return get(
            path = path,
            operation = "Get records"
        ) {
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
    }

    private suspend inline fun <reified T> get(
        path: String,
        operation: String,
        authenticated: Boolean = true,
        noinline configure: HttpRequestBuilder.() -> Unit = {}
    ): T {
        return execute(
            method = HttpMethod.Get,
            path = path,
            operation = operation,
            authenticated = authenticated,
            configure = configure
        )
    }

    private suspend inline fun <reified T> post(
        path: String,
        operation: String,
        authenticated: Boolean = true,
        noinline configure: HttpRequestBuilder.() -> Unit = {}
    ): T {
        return execute(
            method = HttpMethod.Post,
            path = path,
            operation = operation,
            authenticated = authenticated,
            configure = {
                contentType(ContentType.Application.Json)
                configure()
            }
        )
    }

    private suspend inline fun <reified T> put(
        path: String,
        operation: String,
        authenticated: Boolean = true,
        noinline configure: HttpRequestBuilder.() -> Unit = {}
    ): T {
        return execute(
            method = HttpMethod.Put,
            path = path,
            operation = operation,
            authenticated = authenticated,
            configure = {
                contentType(ContentType.Application.Json)
                configure()
            }
        )
    }

    private suspend fun delete(
        path: String,
        operation: String,
        authenticated: Boolean = true,
        configure: HttpRequestBuilder.() -> Unit = {}
    ) {
        execute<Unit>(
            method = HttpMethod.Delete,
            path = path,
            operation = operation,
            authenticated = authenticated,
            configure = configure
        )
    }

    private suspend inline fun <reified T> execute(
        method: HttpMethod,
        path: String,
        operation: String,
        authenticated: Boolean = true,
        noinline configure: HttpRequestBuilder.() -> Unit = {}
    ): T {
        val url = fullUrl(path)

        try {
            val response = when (method) {
                HttpMethod.Get -> client.get(url) {
                    applyCommonHeaders(authenticated)
                    configure()
                }

                HttpMethod.Post -> client.post(url) {
                    applyCommonHeaders(authenticated)
                    configure()
                }

                HttpMethod.Put -> client.put(url) {
                    applyCommonHeaders(authenticated)
                    configure()
                }

                HttpMethod.Delete -> client.delete(url) {
                    applyCommonHeaders(authenticated)
                    configure()
                }

                else -> error("Unsupported HTTP method: ${method.value}")
            }

            ensureSuccess(
                response = response,
                operation = operation,
                method = method.value,
                url = url
            )

            if (T::class == Unit::class) {
                @Suppress("UNCHECKED_CAST")
                return Unit as T
            }

            return response.body()
        } catch (error: SensumApiException) {
            throw error
        } catch (error: ClientRequestException) {
            throw responseExceptionToSensumApiException(
                operation = operation,
                method = method.value,
                url = url,
                response = error.response,
                cause = error
            )
        } catch (error: ServerResponseException) {
            throw responseExceptionToSensumApiException(
                operation = operation,
                method = method.value,
                url = url,
                response = error.response,
                cause = error
            )
        } catch (error: RedirectResponseException) {
            throw responseExceptionToSensumApiException(
                operation = operation,
                method = method.value,
                url = url,
                response = error.response,
                cause = error
            )
        } catch (error: IOException) {
            throw SensumApiException(
                operation = operation,
                method = method.value,
                url = url,
                serviceMessage = "Cannot reach API Gateway at $url. Check Docker containers, Nginx/API Gateway port and SENSUM_API_BASE_URL.",
                cause = error
            )
        } catch (error: Throwable) {
            throw SensumApiException(
                operation = operation,
                method = method.value,
                url = url,
                serviceMessage = error.message ?: error::class.simpleName.orEmpty(),
                cause = error
            )
        }
    }

    private fun HttpRequestBuilder.applyCommonHeaders(authenticated: Boolean) {
        if (authenticated) {
            header(HttpHeaders.Authorization, "Bearer ${requireToken()}")
        }
    }

    private suspend fun ensureSuccess(
        response: HttpResponse,
        operation: String,
        method: String,
        url: String
    ) {
        if (response.status.isSuccess()) {
            return
        }

        val rawBody = response.bodyAsText()

        throw SensumApiException(
            operation = operation,
            method = method,
            url = url,
            statusCode = response.status.value,
            statusText = response.status.description,
            serviceMessage = rawBody.toUserMessage(response.status),
            rawBody = rawBody
        )
    }

    private suspend fun responseExceptionToSensumApiException(
        operation: String,
        method: String,
        url: String,
        response: HttpResponse,
        cause: Throwable
    ): SensumApiException {
        val rawBody = response.bodyAsText()

        return SensumApiException(
            operation = operation,
            method = method,
            url = url,
            statusCode = response.status.value,
            statusText = response.status.description,
            serviceMessage = rawBody.toUserMessage(response.status),
            rawBody = rawBody,
            cause = cause
        )
    }

    private fun String.toUserMessage(status: HttpStatusCode): String {
        val body = trim()

        if (body.isBlank()) {
            return when (status) {
                HttpStatusCode.Unauthorized -> "You are not logged in or your session expired."
                HttpStatusCode.Forbidden -> "You do not have permission for this action."
                HttpStatusCode.NotFound -> "Requested resource was not found."
                else -> "Service returned HTTP ${status.value} ${status.description}."
            }
        }

        val apiError = runCatching {
            json.parseToJsonElement(body)
                .jsonObject["error"]
                ?.jsonPrimitive
                ?.content
        }.getOrNull()

        return apiError ?: body
    }

    private fun requireToken(): String {
        return session.requireToken()
    }

    private fun fullUrl(path: String): String {
        return baseUrl.trimEnd('/') + "/" + path.trimStart('/')
    }
}