// apps/desktop-demo/src/main/kotlin/si/sensum/demo/api/SensumApiException.kt
package si.sensum.demo.api

class SensumApiException(
    val operation: String,
    val method: String,
    val url: String,
    val statusCode: Int? = null,
    val statusText: String? = null,
    val serviceMessage: String,
    val rawBody: String? = null,
    cause: Throwable? = null
) : RuntimeException(
    buildString {
        append(operation)
        append(" failed")

        if (statusCode != null) append(": HTTP ").append(statusCode)
        if (!statusText.isNullOrBlank()) append(" ").append(statusText)

        append("\n")
        append(serviceMessage)

        append("\n\nCall stack:")
        append("\n")
        append(method)
        append(" ")
        append(url)

        if (!rawBody.isNullOrBlank() && rawBody != serviceMessage) {
            append("\n\nRaw response:")
            append("\n")
            append(rawBody)
        }
    },
    cause
)