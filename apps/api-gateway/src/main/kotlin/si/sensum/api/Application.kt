package si.sensum.api

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.api.config.loadApiGatewayConfig
import si.sensum.api.di.ApiDependencies
import si.sensum.api.di.createApiDependencies
import si.sensum.api.plugins.installApiJwtAuthentication
import si.sensum.api.plugins.installApiPlugins
import si.sensum.api.routes.apiRoutes
import si.sensum.shared.models.api.HealthResponse

private const val SERVICE_NAME = "api-gateway"

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val config = loadApiGatewayConfig()
    val dependencies = createApiDependencies(config)

    installApiPlugins()
    installApiJwtAuthentication(
        config = config,
        jwtTokenService = dependencies.jwtTokenService
    )

    configureRoutes(dependencies)
}

private fun Application.configureRoutes(
    dependencies: ApiDependencies
) {
    routing {
        healthRoute()

        route("/api/v1") {
            apiRoutes(dependencies)
        }
    }
}

private fun Route.healthRoute() {
    get("/health") {
        call.respond(
            HealthResponse(
                status = "ok",
                service = SERVICE_NAME
            )
        )
    }
}