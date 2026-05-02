package si.sensum.gm.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import si.sensum.gm.auth.resolveUserSessionOrRespond
import si.sensum.gm.model.MeasurementQuery
import si.sensum.gm.services.AuthService
import si.sensum.shared.models.api.ApiErrorResponse
import si.sensum.sws.SmartWebSoapClient
import si.sensum.sws.model.SwsSession

fun Route.measurementRoutes(
    authService: AuthService,
    soapClient: SmartWebSoapClient
) {
    route("/api/v1") {
        get("/measurements") {
            val userSession = call.resolveUserSessionOrRespond(authService) ?: return@get

            val query = call.extractMeasurementQueryOrRespond() ?: return@get

            val swsSession = SwsSession(
                cookieName = userSession.swsCookieName,
                cookieValue = userSession.swsCookieValue
            )

            val measurements = soapClient.getAllMeasurements(
                session = swsSession,
                datetimeFrom = query.datetimeFrom,
                datetimeTo = query.datetimeTo
            )

            call.respond(measurements)
        }
    }
}

private suspend fun ApplicationCall.extractMeasurementQueryOrRespond(): MeasurementQuery? {
    val datetimeFrom = request.queryParameters["datetimeFrom"]
    val datetimeTo = request.queryParameters["datetimeTo"]

    if (datetimeFrom.isNullOrBlank() || datetimeTo.isNullOrBlank()) {
        respond(
            HttpStatusCode.BadRequest,
            ApiErrorResponse("Missing required query parameters: datetimeFrom, datetimeTo")
        )
        return null
    }

    return try {
        MeasurementQuery(
            datetimeFrom = datetimeFrom,
            datetimeTo = datetimeTo
        )
    } catch (e: Exception) {
        respond(
            HttpStatusCode.BadRequest,
            ApiErrorResponse("Invalid datetime format. Use 2026-02-01T00:00:00 or 2026-02-01T00:00:00+01:00")
        )
        null
    }
}