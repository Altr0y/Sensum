package si.sensum.gm.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import org.slf4j.event.Level
import si.sensum.gm.routes.GmApiRoutes
import si.sensum.logging.Logger
import java.util.UUID

fun Application.installGmPlugins() {
    installGmErrorHandling()

    install(ContentNegotiation) {
        json()
    }

    install(CallId) {
        generate { UUID.randomUUID().toString() }
        verify { callId -> callId.isNotBlank() }
        replyToHeader("X-Request-Id")
    }

    install(CallLogging) {
        level = Level.INFO

        filter { call ->
            call.request.path().startsWith(GmApiRoutes.BASE) ||
                    call.request.path() == "/health"
        }

        format { call ->
            val method = call.request.httpMethod.value
            val normalizedPath = "/" + call.request.path().trimStart('/')
            "$method $normalizedPath"
        }

        callIdMdc("requestId")
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        val start = System.currentTimeMillis()

        proceed()

        val duration = System.currentTimeMillis() - start
        val status = call.response.status()?.value ?: 0
        val method = call.request.httpMethod.value
        val normalizedPath = "/" + call.request.path().trimStart('/')

        Logger.log.info {
            "[HTTP] $method $normalizedPath status=$status duration=${duration}ms"
        }
    }
}