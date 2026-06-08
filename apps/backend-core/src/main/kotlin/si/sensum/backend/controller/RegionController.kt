package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.backend.service.RegionService
import kotlin.text.toIntOrNull
import io.ktor.server.application.call

fun Route.configureRegionRoutes(
    regionService: RegionService
) {
    route("/api/v1/regions") {
        get {
            val regions = regionService.getAllRegions()

            call.respond(
                HttpStatusCode.OK,
                regions
            )
        }

        get("/{regionId}") {
            val regionId = call.parameters["regionId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid region id")

            val region = regionService.getRegion (regionId)

            call.respond(
                HttpStatusCode.OK,
                region
            )
        }
    }

    route("/api/v1/countries/{countryId}/regions") {
        get {
            val countryId = call.parameters["countryId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid country id")

            val regions = regionService.getRegionsByCountry(countryId)

            call.respond(
                HttpStatusCode.OK,
                regions
            )
        }
    }
}