package si.sensum.logging

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.plugins.callid.callId
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import org.slf4j.MDC
import java.util.UUID

fun Application.installHttpRequestLogging(
    serviceName: String,
    shouldLogPath: (String) -> Boolean = { path ->
        path.startsWith("/api/") || path == "/health"
    }
) {
    intercept(ApplicationCallPipeline.Monitoring) {
        val requestId = call.callId ?: UUID.randomUUID().toString()
        val path = call.request.path().normalizedPath()

        MDC.put("requestId", requestId.shortRequestId())

        val start = System.currentTimeMillis()

        try {
            proceed()
        } finally {
            val duration = System.currentTimeMillis() - start
            val status = call.response.status()?.value

            if (shouldLogPath(path) && status != null) {
                Logger.log.info {
                    "[HTTP] service=$serviceName method=${call.request.httpMethod.value} path=$path status=$status duration=${duration}ms"
                }
            }

            MDC.remove("requestId")
        }
    }
}

private fun String.normalizedPath(): String {
    return "/" + trimStart('/')
}