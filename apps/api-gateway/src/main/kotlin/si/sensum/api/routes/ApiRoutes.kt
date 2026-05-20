package si.sensum.api.routes

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import si.sensum.api.di.ApiDependencies

internal fun Route.apiRoutes(
    dependencies: ApiDependencies
) {
    publicRoutes(dependencies)

    authenticate("auth-jwt") {
        protectedRoutes(dependencies)
    }
}

private fun Route.publicRoutes(
    dependencies: ApiDependencies
) {
    publicAuthRoutes(
        authService = dependencies.authService
    )
}

private fun Route.protectedRoutes(
    dependencies: ApiDependencies
) {
    protectedAuthRoutes(
        authService = dependencies.authService
    )

    measurementRoutes(
        backendMeasurements = dependencies.backendMeasurements
    )

    stationRoutes(
        backendStations = dependencies.backendStations
    )

    channelRoutes(
        backendChannels = dependencies.backendChannels
    )
}