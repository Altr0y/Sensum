package si.sensum.geodsl

import si.sensum.geodsl.ast.ProgramNode
import si.sensum.geodsl.export.export
import si.sensum.geodsl.import.*
import si.sensum.geodsl.lexer.Lexer
import si.sensum.geodsl.parser.Parser
import java.io.File

fun main() {
    try {
        val inputDir = File("test-data/geojson")
        val sensumOutputDir = File("test-data/output/sensum")
        val geoJsonOutputDir = File("test-data/output/geojson")

        sensumOutputDir.mkdirs()
        geoJsonOutputDir.mkdirs()

        val inputFiles = inputDir
            .listFiles { file -> file.extension.lowercase() == "geojson" }
            ?.toList()
            ?: emptyList()

        if (inputFiles.isEmpty()) {
            println("No GeoJSON files found in ${inputDir.absolutePath}")
            return
        }

        for (geoJsonFile in inputFiles) {
            val type = detectGeoType(geoJsonFile)

            if (type == null) {
                println("Skipping unknown GeoJSON type: ${geoJsonFile.name}")
                continue
            }

            println("Processing ${geoJsonFile.name} as $type")

            val baseName = geoJsonFile.nameWithoutExtension

            val sensumBody = when (type) {
                GeoType.RIVER -> convertRivers(geoJsonFile.path)
                GeoType.LAKE -> convertLakes(geoJsonFile.path)
                GeoType.REGION -> convertRegions(geoJsonFile.path)
                GeoType.MUNICIPALITY -> convertMunicipalities(geoJsonFile.path)
                GeoType.FLOOD_ZONE -> convertFloodZones(geoJsonFile.path, detectFloodRisk(geoJsonFile))
            }

            val sensumFile = File(sensumOutputDir, "$baseName.sensum")
            sensumFile.writeText(sensumBody)

            val program = when (type) {
                GeoType.MUNICIPALITY -> parseMunicipalitySensum(sensumBody)
                else -> parseSensumBody(sensumBody)
            }

            val geoJson = export(program)

            val outputGeoJsonFile = File(geoJsonOutputDir, "$baseName.geojson")
            outputGeoJsonFile.writeText(geoJson)

            println("  Sensum:  ${sensumFile.path}")
            println("  GeoJSON: ${outputGeoJsonFile.path}")
        }

        println("Done.")

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Parse a raw sensum geo body (no country/layer wrapper) straight to a ProgramNode
private fun parseMunicipalitySensum(sensumBody: String): ProgramNode {
    val wrapped = """
        country "Slovenia" {
        ${sensumBody}
        }
    """.trimIndent()
    val lexer = Lexer(wrapped)
    val tokens = lexer.tokenize()
    return Parser(tokens, lexer).parseProgram()
}

private fun parseSensumBody(sensumBody: String): ProgramNode {
    val lexer = Lexer(sensumBody)
    val tokens = lexer.tokenize()
    return Parser(tokens, lexer).parseProgram()
}

//finds risk from filename or returns null
private fun detectFloodRisk(file: File): String? {
    val name = file.name.lowercase()
    return when {
        "very rare" in name || "zelo redke" in name -> "very_rare"
        "rare"      in name || "redke"      in name -> "rare"
        "often"     in name || "pogoste"    in name -> "often"
        else -> null
    }
}

private fun detectGeoType(file: File): GeoType? {
    val name = file.name.lowercase()
    return when {
        "river" in name || "reke" in name || "vodotok" in name -> GeoType.RIVER
        "lake" in name || "jezer" in name -> GeoType.LAKE
        "region" in name || "regij" in name -> GeoType.REGION
        "municip" in name || "obcin" in name || "občin" in name -> GeoType.MUNICIPALITY
        "flood" in name || "poplav" in name -> GeoType.FLOOD_ZONE
        else -> null
    }
}