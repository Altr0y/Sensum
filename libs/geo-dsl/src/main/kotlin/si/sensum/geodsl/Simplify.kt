package si.sensum.geodsl

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import si.sensum.geodsl.import.GeoType
import java.io.File

fun main() {
    val inputDir = File("test-data/geojson")
    val outputDir = File("test-data/output/simplified")
    outputDir.mkdirs()

    val files = mapOf(
        "Floods - Often.geojson" to Pair(GeoType.FLOOD_ZONE, "often"),
        "Floods - Rare.geojson" to Pair(GeoType.FLOOD_ZONE, "rare"),
        "Floods - Very rare.geojson" to Pair(GeoType.FLOOD_ZONE, "very_rare"),
        "Rivers.geojson" to Pair(GeoType.RIVER, null),
        "Regions.geojson" to Pair(GeoType.REGION, null),
        "Municipalities.geojson" to Pair(GeoType.MUNICIPALITY, null),
    )

    for ((fileName, meta) in files) {
        val (type, risk) = meta
        val inputFile = File(inputDir, fileName)
        if (!inputFile.exists()) {
            println("Skipping $fileName — not found")
            continue
        }

        println("Processing $fileName (${inputFile.length() / 1024}KB)...")

        val simplified = simplifyGeoJson(inputFile, type, risk)
        val outputFile = File(outputDir, fileName)
        outputFile.writeText(simplified)

        println("  Output: ${outputFile.length() / 1024}KB")
    }

    println("Done.")
}

fun simplifyGeoJson(file: File, type: GeoType, risk: String?): String {
    val stepSize = when (file.length()) {
        in 0..5_000_000 -> 2    // do 5MB   — vsaka 5. točka
        in 5_000_000..20_000_000 -> 5  // 5-20MB   — vsaka 20. točka
        else -> 10   // nad 20MB — vsaka 50. točka
    }

    val inputJson = kotlinx.serialization.json.Json
        .parseToJsonElement(file.readText())
        .jsonObject

    val features = inputJson["features"]?.jsonArray
        ?: return ""

    val simplifiedFeatures = buildString {
        appendLine("""{"type":"FeatureCollection","features":[""")
        features.forEachIndexed { i: Int, featureEl: JsonElement ->
            val feature = featureEl.jsonObject
            val geometry = feature["geometry"]?.jsonObject ?: return@forEachIndexed
            val geometryType = geometry["type"]?.jsonPrimitive?.content ?: return@forEachIndexed
            val coordinates = geometry["coordinates"] ?: return@forEachIndexed
            val properties = feature["properties"]?.jsonObject
                ?: kotlinx.serialization.json.buildJsonObject {}

            val simplifiedCoords = simplifyCoordinates(coordinates, geometryType, stepSize)

            if (i > 0) append(",")
            appendLine("""{"type":"Feature","properties":${properties},"geometry":{"type":"$geometryType","coordinates":$simplifiedCoords}}""")
        }
        appendLine("]}")
    }

    return simplifiedFeatures
}

fun simplifyCoordinates(
    coordinates: JsonElement,
    geometryType: String,
    step: Int
): String {
    return when (geometryType) {
        "Polygon" -> {
            val rings = coordinates.jsonArray.map { ring: JsonElement ->
                val allPoints = ring.jsonArray
                // Ce ima poligon malo tock, ohrani vse
                val points = if (allPoints.size < 50)
                    allPoints.toList()
                else
                    allPoints.filterIndexed { i: Int, _: JsonElement -> i % step == 0 }
                JsonArray(points)
            }
            JsonArray(rings).toString()
        }

        "MultiPolygon" -> {
            val polys = coordinates.jsonArray.map { poly: JsonElement ->
                val rings = poly.jsonArray.map { ring: JsonElement ->
                    val allPoints = ring.jsonArray
                    val points = if (allPoints.size < 50)
                        allPoints.toList()
                    else
                        allPoints.filterIndexed { i: Int, _: JsonElement -> i % step == 0 }
                    JsonArray(points)
                }
                JsonArray(rings)
            }
            JsonArray(polys).toString()
        }

        "LineString" -> {
            val points = coordinates.jsonArray
                .filterIndexed { i: Int, _: JsonElement -> i % step == 0 }
            JsonArray(points).toString()
        }

        "MultiLineString" -> {
            val lines = coordinates.jsonArray.map { line: JsonElement ->
                val points = line.jsonArray
                    .filterIndexed { i: Int, _: JsonElement -> i % step == 0 }
                JsonArray(points)
            }
            JsonArray(lines).toString()
        }

        else -> coordinates.toString()
    }
}