package si.sensum.geodsl.import

import kotlinx.serialization.json.*
import java.io.File

fun geoJsonToSensum(geoJsonFile: File, type: GeoType, explicitRisk: String? = null): String {
    val json = Json.parseToJsonElement(geoJsonFile.readText()).jsonObject
    val  features = json["features"]?.jsonArray ?: return ""

    val result = StringBuilder()

    for((index, featureElement) in features.withIndex()) {
        val feature = featureElement.jsonObject
        val properties = feature["properties"]?.jsonObject ?: buildJsonObject {}
        val geometry = feature["geometry"]?.jsonObject ?: continue

        val name = extractName(properties, type, index)
        val geometryType = geometry["type"]?.jsonPrimitive?.content ?: continue
        val coordinates = geometry["coordinates"] ?: continue

        when(type){
            GeoType.RIVER -> {
                appendLineGeometry(result, "river", name, coordinates, geometryType)
            }

            GeoType.LAKE -> {
                appendPolygonGeometry(result, "lake", name, null, coordinates, geometryType)
            }

            GeoType.REGION -> {
                appendPolygonGeometry(result, "region", name, null, coordinates, geometryType)
            }

            GeoType.MUNICIPALITY ->{
                appendMunicipality(result, name, coordinates, geometryType)
            }

            GeoType.FLOOD_ZONE -> {
                val risk = explicitRisk ?: extractFloodRisk(properties)
                appendPolygonGeometry(result, "flood_zone", name, risk, coordinates, geometryType)
            }
        }

        result.appendLine()
    }

    return result.toString()
}

fun convertRivers(path: String): String = geoJsonToSensum(File(path), GeoType.RIVER)

fun convertLakes(path: String): String = geoJsonToSensum(File(path), GeoType.LAKE)

fun convertRegions(path: String): String = geoJsonToSensum(File(path), GeoType.REGION)

fun convertMunicipalities(path: String): String = geoJsonToSensum(File(path), GeoType.MUNICIPALITY)

fun convertFloodZones(path: String, risk: String?): String = geoJsonToSensum(File(path), GeoType.FLOOD_ZONE, explicitRisk = risk)

private fun extractName (
    properties: JsonObject,
    type: GeoType,
    index: Int
): String {
    return when (type){
        GeoType.RIVER, GeoType.LAKE -> {
            properties.stringOrNull("VTPV_IME")
                ?: properties.stringOrNull("IME")
                ?: properties.stringOrNull("NAME")
                ?: "${type.name.lowercase()}_$index"
        }

        GeoType.REGION, GeoType.MUNICIPALITY -> {
            properties.stringOrNull("NAZIV")
                ?: properties.stringOrNull("IME")
                ?: properties.stringOrNull("NAME")
                ?: "${type.name.lowercase()}_$index"
        }

        // Each flood file uses a different property for the zone name:
        // Often    -> PP_IME
        // Rare     -> RP_IME
        // Very rare-> ZR_IME
        GeoType.FLOOD_ZONE -> {
            properties.stringOrNull("PP_IME")
                ?: properties.stringOrNull("RP_IME")
                ?: properties.stringOrNull("ZR_IME")
                ?: properties.stringOrNull("IME")
                ?: "flood_zone_$index"
        }
    }
}

private fun extractFloodRisk(properties: JsonObject): String {
    val value  = (
            properties.stringOrNull("PP_IME")
                ?: properties.stringOrNull("RP_IME")
                ?: properties.stringOrNull("ZR_IME")
            )?.lowercase() ?: return "unknown"

    return when {
        "zelo redke" in value -> "very_rare"
        "redke" in value      -> "rare"
        "pogoste" in value    -> "often"
        else                  -> "unknown"
    }
}

private fun appendLineGeometry(
    result: StringBuilder,
    keyword: String,
    name: String,
    coordinates: JsonElement,
    geometryType: String
) {
    when (geometryType){
        "LineString" -> {
            result.appendLine("""$keyword "${escape(name)}" line { """)
            appendPoints(result, coordinates.jsonArray)
            result.appendLine("}")
        }

        "MultiLineString" -> {
            for ((i, line) in coordinates.jsonArray.withIndex()) {
                val finalName = if ( coordinates.jsonArray.size > 1) "$name part ${i + 1}" else name
                result.appendLine("""$keyword "${escape(finalName)}" line {""")
                appendPoints(result, line.jsonArray)
                result.appendLine("}")
            }
        }
    }
}

private fun appendPolygonGeometry(
    result: StringBuilder,
    keyword: String,
    name: String,
    risk: String?,
    coordinates: JsonElement,
    geometryType: String
){
    when(geometryType){
        "Polygon" -> {
            val header =
                if(risk == null) """$keyword "${escape(name)}" polygon {"""
                else """$keyword "${escape(name)}" risk $risk polygon {"""

            result.appendLine(header)
            appendPoints(result, coordinates.jsonArray.first().jsonArray)
            result.appendLine("}")
        }

        "MultiPolygon" -> {
            for ((i, polygon) in coordinates.jsonArray.withIndex()) {
                val finalName = if (coordinates.jsonArray.size > 1) "$name part ${i + 1}" else name
                val outerRing = polygon.jsonArray.first().jsonArray

                val header =
                    if (risk == null) """$keyword "${escape(finalName)}" polygon {"""
                    else """$keyword "${escape(finalName)}" risk $risk polygon {"""

                result.appendLine(header)
                appendPoints(result, outerRing)
                result.appendLine("}")
            }
        }
    }
}

private fun appendMunicipality(
    result: StringBuilder,
    name: String,
    coordinates: JsonElement,
    geometryType: String
){
    when(geometryType){
        "Polygon" -> {
            result.appendLine("""municipality "${escape(name)}" polygon {""")
            appendPoints(result, coordinates.jsonArray.first().jsonArray)
            result.appendLine("}")
        }

        "MultiPolygon" -> {
            for ((i, polygon) in coordinates.jsonArray.withIndex()) {
                val finalName = if (coordinates.jsonArray.size > 1) "$name part ${i + 1}" else name
                val outerRing = polygon.jsonArray.first().jsonArray

                result.appendLine("""municipality "${escape(finalName)}" polygon {""")
                appendPoints(result, outerRing)
                result.appendLine("}")
            }
        }
    }
}

private fun appendPoints(
    result: StringBuilder,
    points: JsonArray
){
    for (pointElement in points) {
        val point = pointElement.jsonArray
        val lon = point[0].jsonPrimitive.double
        val lat = point[1].jsonPrimitive.double

        result.appendLine("    point($lon, $lat);")
    }
}

private fun JsonObject.stringOrNull(key: String): String? {
    return this[key]?.jsonPrimitive?.contentOrNull
}

private fun escape(value: String): String{
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
}
