package si.sensum.api

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level
import si.sensum.api.auth.StaticApiTokenValidator
import si.sensum.api.backend.BackendClient
import si.sensum.api.config.ApiGatewayConfig
import si.sensum.api.config.createHttpClient
import si.sensum.api.routes.authRoutes
import si.sensum.api.routes.measurementRoutes
import si.sensum.logging.Logger
import si.sensum.shared.models.api.HealthResponse
import java.util.UUID

object ApiInfo {
    const val NAME = "API Gateway"
}

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val appConfig = loadAppConfig()

    installPlugins()

    val httpClient = createHttpClient()

    val backendClient = BackendClient(
        httpClient = httpClient,
        baseUrl = appConfig.backendCoreBaseUrl
    )

    val apiTokenValidator = StaticApiTokenValidator(
        expectedToken = appConfig.demoAuthToken
    )

    configureRoutes(
        backendClient = backendClient,
        tokenValidator = apiTokenValidator,
        appConfig = appConfig
    )
}

private fun Application.loadAppConfig(): ApiGatewayConfig {
    val config = environment.config

    val backendCoreBaseUrl = config.property("backendCore.baseUrl").getString()
    val demoUsername = config.property("demo.username").getString()
    val demoPassword = config.property("demo.password").getString()
    val demoAuthToken = config.property("demo.authToken").getString()

    require(backendCoreBaseUrl.isNotBlank()) {
        "Missing backendCore baseUrl. Set BACKEND_CORE_BASE_URL env variable."
    }

    require(demoAuthToken.isNotBlank()) {
        "Missing demo auth token. Set DEMO_AUTH_TOKEN env variable."
    }

    return ApiGatewayConfig(
        backendCoreBaseUrl = backendCoreBaseUrl,
        demoUsername = demoUsername,
        demoPassword = demoPassword,
        demoAuthToken = demoAuthToken
    )
}

private fun Application.installPlugins() {
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
            call.request.path().startsWith("/api/") || call.request.path() == "/health"
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

private fun Application.configureRoutes(
    backendClient: BackendClient,
    tokenValidator: StaticApiTokenValidator,
    appConfig: ApiGatewayConfig
) {
    routing {
        get("/health") {
            call.respond(
                HealthResponse(
                    status = "ok",
                    service = "api-gateway"
                )
            )
        }

        authRoutes(
            demoUsername = appConfig.demoUsername,
            demoPassword = appConfig.demoPassword,
            demoAuthToken = appConfig.demoAuthToken
        )

        measurementRoutes(
            backendClient = backendClient,
            tokenValidator = tokenValidator
        )
    }
}