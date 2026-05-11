package si.sensum.gm.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.gm.auth.resolveSwsSessionOrRespond
import si.sensum.gm.model.MeasurementQuery
import si.sensum.gm.services.AuthService
import si.sensum.gm.services.MeasurementService
import si.sensum.shared.models.api.ApiErrorResponse
import io.ktor.server.request.receive
import si.sensum.shared.models.api.measurements.RefreshMeasurementsRequest


fun Route.measurementRoutes(
    authService: AuthService,
    measurementService: MeasurementService
) {
    get("/measurements") {
        val swsSession = call.resolveSwsSessionOrRespond(authService) ?: return@get
        val query = call.extractMeasurementQueryOrRespond() ?: return@get

        val measurements = measurementService.getMeasurements(
            session = swsSession,
            query = query
        )

        call.respond(measurements)
    }

    post("/measurements/by-station-channel-pairs") {
        val session = call.resolveSwsSessionOrRespond(authService) ?: return@post
        val request = call.receive<RefreshMeasurementsRequest>()

        val measurements = measurementService.getMeasurementsByStationChannelPairs(
            session = session,
            request = request
        )

        call.respond(HttpStatusCode.OK, measurements)
    }
}

private suspend fun ApplicationCall.extractMeasurementQueryOrRespond(): MeasurementQuery? {
    val datetimeFrom = request.queryParameters["datetimeFrom"]
    val datetimeTo = request.queryParameters["datetimeTo"]

    if (datetimeFrom.isNullOrBlank() || datetimeTo.isNullOrBlank()) {
        respond(
            HttpStatusCode.BadRequest,
            ApiErrorResponse(
                error = "Missing required query parameters: datetimeFrom, datetimeTo"
            )
        )
        return null
    }

    return MeasurementQuery(
        datetimeFrom = datetimeFrom,
        datetimeTo = datetimeTo
    )
}