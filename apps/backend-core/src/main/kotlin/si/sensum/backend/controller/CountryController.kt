package si.sensum.backend.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.backend.service.CountryService
import kotlin.text.toIntOrNull
import io.ktor.server.response.respond
import io.ktor.server.application.call

fun Route.configureCountryRoutes(
    countryService: CountryService
) {
    route("/api/v1/countries") {
        get {
            val countries = countryService.getAllCountries()

            call.respond(
                HttpStatusCode.OK,
                countries
            )
        }

        get("/{countryId}") {
            val countryId = call.parameters["countryId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid country id")

            val country = countryService.getCountry(countryId)

            call.respond(
                HttpStatusCode.OK,
                country
            )
        }
    }
}