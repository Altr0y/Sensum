package si.sensum.gm

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.gm.config.loadGmAppConfig
import si.sensum.gm.di.GmDependencies
import si.sensum.gm.di.createGmDependencies
import si.sensum.gm.plugins.installGmPlugins
import si.sensum.gm.routes.authRoutes
import si.sensum.gm.routes.measurementRoutes
import si.sensum.shared.models.api.HealthResponse
import si.sensum.gm.routes.GmApiRoutes

object ApiInfo {
    const val NAME = "Eltratec GM"
}

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val config = loadGmAppConfig()
    val dependencies = createGmDependencies(config)

    installGmPlugins()
    configureRoutes(dependencies)
}

private fun Application.configureRoutes(
    dependencies: GmDependencies
) {
    routing {
        get("/health") {
            call.respond(
                HealthResponse(
                    status = "ok",
                    service = "eltratec-gm"
                )
            )
        }

        route(GmApiRoutes.BASE) {
            authRoutes(
                authService = dependencies.authService,
                tokenValidator = dependencies.gmTokenValidator
            )

            measurementRoutes(
                authService = dependencies.authService,
                measurementService = dependencies.measurementService
            )
        }
    }
}