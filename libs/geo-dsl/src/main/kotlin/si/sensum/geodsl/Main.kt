package si.sensum.geodsl

import si.sensum.geodsl.export.export
import si.sensum.geodsl.import.convertGeoJsonDirToSensum
import si.sensum.geodsl.lexer.Lexer
import si.sensum.geodsl.parser.Parser
import si.sensum.geodsl.validator.Validator
import java.io.File

fun main() {
    try {
        val inputDir         = File("test-data/geojson")
        val sensumOutputDir  = File("test-data/output/sensum")
        val geoJsonOutputDir = File("test-data/output/geojson")

        geoJsonOutputDir.mkdirs()

        // Step 1: GeoJSON → sensum files (raw layers + master slovenia.sensum)
        val masterSensum = convertGeoJsonDirToSensum(inputDir, sensumOutputDir)
        println("Sensum files written to: ${sensumOutputDir.path}")

        // Step 2: Lex + parse the master sensum (resolveLayer loads each layer file)
        val lexer   = Lexer(masterSensum, "slovenia.sensum", sensumOutputDir)
        val tokens  = lexer.tokenize()
        val program = Parser(tokens, lexer).parseProgram()

        // Step 3: Export back to GeoJSON
        val geoJson    = export(program)
        val outputFile = File(geoJsonOutputDir, "slovenia.geojson")
        outputFile.writeText(geoJson)
        println("GeoJSON written to: ${outputFile.path}")

        println("\nTEST: slovenia.sensum")
        val errors = Validator().validate(program)
        if (errors.isNotEmpty()) {
            errors.forEach { println("  ValidationError: ${it.message}") }
        } else {
            println("  Validacija: ok")
        }

        println("Done.")

    } catch (e: Exception) {
        e.printStackTrace()
    }
}