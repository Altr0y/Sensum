package si.sensum.api.controller

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import si.sensum.api.di.ApiDependencies

internal fun Route.apiController(
    dependencies: ApiDependencies
) {
    publicControllers(dependencies)

    authenticate("auth-jwt") {
        protectedControllers(dependencies)
    }
}

private fun Route.publicControllers(
    dependencies: ApiDependencies
) {
    publicAuthController(
        authService = dependencies.authService,
        jwtTokenService = dependencies.jwtTokenService
    )
}

private fun Route.protectedControllers(
    dependencies: ApiDependencies
) {
    protectedAuthController(
        authService = dependencies.authService
    )

    customerController(
        customerService = dependencies.customerService
    )

    userController(
        userService = dependencies.userService
    )

    measurementController(
        measurementService = dependencies.measurementService
    )

    stationController(
        stationService = dependencies.stationService
    )

    channelController(
        channelService = dependencies.channelService
    )

    recordsController(
        recordsService = dependencies.recordsService
    )

    dslController(
        dslService = dependencies.dslService
    )

    simulatorController(
        simulatorService = dependencies.simulatorService
    )

    statsController(
        statsService = dependencies.statsService
    )
}