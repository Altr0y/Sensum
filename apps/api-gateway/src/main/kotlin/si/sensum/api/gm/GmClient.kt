package si.sensum.api.gm

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.shared.models.api.LoginRequest
import si.sensum.shared.models.api.LoginResponse

class GmClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val gmAuthToken: String
) {

    suspend fun login(request: LoginRequest): LoginResponse {
        val response = httpClient.post("$baseUrl/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $gmAuthToken")
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            val error = runCatching {
                response.body<ApiErrorResponse>().error
            }.getOrElse {
                "GM login failed with HTTP ${response.status.value}"
            }

            throw GmClientException(
                status = response.status,
                message = error
            )
        }

        return response.body()
    }
}

class GmClientException(
    val status: HttpStatusCode,
    message: String
) : RuntimeException(message)