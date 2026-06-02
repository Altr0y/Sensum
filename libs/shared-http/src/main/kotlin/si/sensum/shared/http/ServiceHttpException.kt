package si.sensum.shared.http

import io.ktor.http.HttpStatusCode

class ServiceHttpException(
    val statusCode: HttpStatusCode,
    val responseBody: String
) : RuntimeException("Service HTTP request failed with status $statusCode: $responseBody")