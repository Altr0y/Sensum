package si.sensum.backend.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import si.sensum.logging.installHttpRequestLogging
import java.util.UUID

fun Application.configureBackendPlugins() {
    installBackendErrorHandling()

    install(ContentNegotiation) {
        json()
    }

    install(CallId) {
        generate { UUID.randomUUID().toString() }
        verify { callId -> callId.isNotBlank() }
        replyToHeader("X-Request-Id")
    }

    installHttpRequestLogging(
        serviceName = "backend-core"
    )
}