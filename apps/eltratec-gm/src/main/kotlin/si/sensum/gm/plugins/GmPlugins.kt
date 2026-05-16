package si.sensum.gm.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.contentnegotiation.*
import si.sensum.gm.routes.GmApiRoutes
import si.sensum.logging.installHttpRequestLogging
import java.util.UUID

private const val SERVICE_NAME = "eltratec-gm"

internal fun Application.installGmPlugins() {
    installGmErrorHandling()

    install(ContentNegotiation) {
        json()
    }

    install(CallId) {
        generate { UUID.randomUUID().toString() }
        verify { callId -> callId.isNotBlank() }
        replyToHeader("X-Request-Id")
    }

    installHttpRequestLogging(
        serviceName = SERVICE_NAME,
        shouldLogPath = { path ->
            path.startsWith(GmApiRoutes.BASE) || path == "/health"
        }
    )
}