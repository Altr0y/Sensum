package si.sensum.api

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import si.sensum.api.config.loadApiGatewayConfig
import si.sensum.api.controller.apiController
import si.sensum.api.controller.healthController
import si.sensum.api.di.ApiDependencies
import si.sensum.api.di.createApiDependencies
import si.sensum.api.plugins.installApiJwtAuthentication
import si.sensum.api.plugins.installApiPlugins

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val config = loadApiGatewayConfig()
    val dependencies = createApiDependencies(config)

    writeJwksFile(dependencies.jwtTokenService.jwksJson())

    installApiPlugins()
    installApiJwtAuthentication(
        config = config,
        jwtTokenService = dependencies.jwtTokenService
    )

    configureRoutes(dependencies)
}

private fun writeJwksFile(json: String?) {
    val path = System.getenv("JWKS_FILE_PATH")?.takeIf { it.isNotBlank() } ?: return
    if (json == null) return
    val file = java.io.File(path)
    file.parentFile?.mkdirs()
    file.writeText(json)
}

private fun Application.configureRoutes(
    dependencies: ApiDependencies
) {
    routing {
        healthController()

        route("/api/v1") {
            apiController(dependencies)
        }
    }
}