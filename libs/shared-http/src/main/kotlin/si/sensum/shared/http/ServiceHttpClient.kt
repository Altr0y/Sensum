package si.sensum.shared.http

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.client.request.patch
import io.ktor.http.isSuccess

class ServiceHttpClient(
    @PublishedApi
    internal val httpClient: HttpClient,

    @PublishedApi
    internal val baseUrl: String
) {
    suspend inline fun <reified T> get(
        path: String,
        bearerToken: String? = null
    ): T {
        val response = httpClient.get(fullUrl(path)) {
            bearerToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }

        if (!response.status.isSuccess()) {
            throw ServiceHttpException(
                statusCode = response.status,
                responseBody = response.bodyAsText()
            )
        }

        return response.body()
    }

    suspend inline fun <reified T> post(
        path: String,
        body: Any,
        bearerToken: String? = null
    ): T {
        val response = httpClient.post(fullUrl(path)) {
            contentType(ContentType.Application.Json)

            bearerToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }

            setBody(body)
        }

        if (!response.status.isSuccess()) {
            throw ServiceHttpException(
                statusCode = response.status,
                responseBody = response.bodyAsText()
            )
        }

        return response.body()
    }

    suspend inline fun <reified T> put(
        path: String,
        body: Any,
        bearerToken: String? = null
    ): T {
        val response = httpClient.put(fullUrl(path)) {
            contentType(ContentType.Application.Json)

            bearerToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }

            setBody(body)
        }

        if (!response.status.isSuccess()) {
            throw ServiceHttpException(
                statusCode = response.status,
                responseBody = response.bodyAsText()
            )
        }

        return response.body()
    }

    suspend fun delete(
        path: String,
        bearerToken: String? = null
    ) {
        val response = httpClient.delete(fullUrl(path)) {
            bearerToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }

        if (!response.status.isSuccess()) {
            throw ServiceHttpException(
                statusCode = response.status,
                responseBody = response.bodyAsText()
            )
        }
    }

    suspend inline fun <reified T> deleteWithResponse(
        path: String,
        bearerToken: String? = null
    ): T {
        val response = httpClient.delete(fullUrl(path)) {
            bearerToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }

        if (!response.status.isSuccess()) {
            throw ServiceHttpException(
                statusCode = response.status,
                responseBody = response.bodyAsText()
            )
        }

        return response.body()
    }

    suspend inline fun <reified T> patch(
        path: String,
        body: Any,
        bearerToken: String? = null
    ): T {
        val response = httpClient.patch(fullUrl(path)) {
            contentType(ContentType.Application.Json)
            bearerToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            setBody(body)
        }
        if (!response.status.isSuccess()) {
            throw ServiceHttpException(
                statusCode = response.status,
                responseBody = response.bodyAsText()
            )
        }
        return response.body()
    }

    @PublishedApi
    internal fun fullUrl(path: String): String {
        return baseUrl.trimEnd('/') + "/" + path.trimStart('/')
    }
}