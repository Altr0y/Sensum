package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.backend.service.MunicipalityService
import kotlin.text.toIntOrNull

fun Route.configureMunicipalityRoutes(
    municipalityService: MunicipalityService
) {
    route("/api/v1/municipalities") {
        get {
            val municipalities = municipalityService.getAllMunicipalities()

            call.respond(
                HttpStatusCode.OK,
                municipalities
            )
        }

        get("/{municipalityId}") {
            val municipalityId = call.parameters["municipalityId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid municipality id")

            val municipality = municipalityService.getMunicipality (municipalityId)

            call.respond(
                HttpStatusCode.OK,
                municipality
            )
        }
    }

    route("/api/v1/regions/{regionId}/municipalities") {
        get {
            val regionId = call.parameters["regionId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid municipality id")

            val municipalities = municipalityService.getMunicipalitiesByRegion(regionId)

            call.respond(
                HttpStatusCode.OK,
                municipalities
            )
        }
    }
}