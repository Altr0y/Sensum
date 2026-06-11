package si.sensum.shared.models.dsl

import kotlinx.serialization.Serializable

@Serializable
data class DslProcessResult(
    /** Raw GeoJSON FeatureCollection string generated from the DSL source. */
    val geoJson: String,
    /** Number of GeoJSON features produced (stations, rivers, lakes, etc.). */
    val featureCount: Int
)
