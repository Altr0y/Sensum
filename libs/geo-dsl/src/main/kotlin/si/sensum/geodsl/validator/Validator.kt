package si.sensum.geodsl.validator

import si.sensum.geodsl.ast.*

data class ValidationError(val message: String)

class Validator {

    fun validate(program: ProgramNode): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        for (country in program.countries) {
            validateCountry(country, errors)
        }
        return errors
    }

    private fun validateCountry(country: CountryNode, errors: MutableList<ValidationError>) {
        for (layer in country.layers) {
            for (element in layer.elements) {
                validateGeoElement(element, errors)
            }
        }
        for (station in country.stations) {
            validateStation(station, errors)
        }
    }

    private fun validateGeoElement(element: GeoElement, errors: MutableList<ValidationError>) {
        when (element) {
            is RiverNode -> element.points.forEach { validateCoordinate(it, "river '${element.name}'", errors) }
            is LakeNode -> element.points.forEach { validateCoordinate(it, "lake '${element.name}'", errors) }
            is RegionNode -> element.points.forEach { validateCoordinate(it, "area '${element.name}'", errors) }
            is FloodZoneNode -> element.points.forEach {
                validateCoordinate(
                    it,
                    "flood_zone '${element.name}'",
                    errors
                )
            }
        }
    }

    private fun validateStation(station: StationNode, errors: MutableList<ValidationError>) {
        validateCoordinate(station.location, "station ${station.id} '${station.name}'", errors)
    }

    private fun validateCoordinate(point: PointNode, context: String, errors: MutableList<ValidationError>) {
        if (point.lon < 13.3 || point.lon > 16.7) {
            errors.add(ValidationError("longitude ${point.lon} is out of range [13.3, 16.7] (Slovenia) in $context"))
        }
        if (point.lat < 45.4 || point.lat > 46.9) {
            errors.add(ValidationError("latitude ${point.lat} is out of range [45.4, 46.9] (Slovenia) in $context"))
        }
    }
}