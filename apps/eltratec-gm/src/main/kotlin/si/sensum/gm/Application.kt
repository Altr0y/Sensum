package si.sensum.gm

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.netty.EngineMain
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import si.sensum.gm.di.GmDependencies
import si.sensum.gm.di.createGmDependencies
import si.sensum.gm.config.loadGmAppConfig
import si.sensum.gm.plugins.GmAuthNames
import si.sensum.gm.plugins.installGmPlugins
import si.sensum.gm.plugins.installGmServiceJwtAuthentication
import si.sensum.gm.routes.GmApiRoutes
import si.sensum.gm.routes.channelRoutes
import si.sensum.gm.routes.gmLoginRoute
import si.sensum.gm.routes.gmLogoutRoute
import si.sensum.gm.routes.measurementRoutes
import si.sensum.gm.routes.stationRoutes
import si.sensum.shared.models.api.HealthResponse

private const val SERVICE_NAME = "eltratec-gm"

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val config = loadGmAppConfig()
    val dependencies = createGmDependencies(config)

    installGmPlugins()
    installGmServiceJwtAuthentication(config)

    configureRoutes(dependencies)
}

private fun Application.configureRoutes(
    dependencies: GmDependencies
) {
    routing {
        healthRoute()

        route(GmApiRoutes.BASE) {
            serviceJwtProtectedRoutes(dependencies)
            gmSessionProtectedRoutes(dependencies)
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

/**
 * Routes protected with backend-core service JWT.
 *
 * This token only proves that backend-core is allowed to call GM.
 * It is not the same as the GM session token returned after SWS login.
 */
private fun Route.serviceJwtProtectedRoutes(
    dependencies: GmDependencies
) {
    authenticate(GmAuthNames.SERVICE_JWT) {
        gmLoginRoute(
            authService = dependencies.authService
        )
    }
}

/**
 * Routes protected with GM session token.
 *
 * Client sends:
 * Authorization: Bearer <gm-session-token>
 *
 * GM uses this token to find GmUserSession in memory,
 * then converts it to SwsSession and calls SWS with the stored cookie.
 */
private fun Route.gmSessionProtectedRoutes(
    dependencies: GmDependencies
) {
    gmLogoutRoute(
        authService = dependencies.authService
    )

    measurementRoutes(
        authService = dependencies.authService,
        measurementService = dependencies.measurementService
    )

    stationRoutes(
        authService = dependencies.authService,
        stationService = dependencies.stationService
    )

    channelRoutes(
        authService = dependencies.authService,
        channelService = dependencies.channelService
    )
}