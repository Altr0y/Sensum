package si.sensum.api.backend

class BackendHttpException(
    val statusCode: Int,
    val responseBody: String
) : RuntimeException("Backend request failed with HTTP $statusCode")