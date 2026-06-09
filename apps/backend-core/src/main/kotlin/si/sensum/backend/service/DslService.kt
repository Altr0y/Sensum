package si.sensum.backend.service

import si.sensum.geodsl.GeoDslProcessor
import si.sensum.geodsl.ast.ProgramNode
import si.sensum.geodsl.export.export
import si.sensum.geodsl.lexer.LexerException
import si.sensum.geodsl.parser.ParseException
import si.sensum.shared.models.dsl.DslProcessResult

internal class DslService {
    fun processSource(
        source: String
    ): DslProcessResult = ServiceLogger.call(
        service = "dsl",
        operation = "processSource",
        details = "length=${source.length}"
    ) {
        require(source.isNotBlank()) {
            "DSL source must not be blank"
        }

        val program = try {
            GeoDslProcessor.parseString(source)
        } catch (error: LexerException) {
            throw IllegalArgumentException("DSL lexer error: ${error.message}", error)
        } catch (error: ParseException) {
            throw IllegalArgumentException("DSL parse error: ${error.message}", error)
        }

        val geoJson = export(program)

        DslProcessResult(
            geoJson = geoJson,
            featureCount = countFeatures(program)
        )
    }

    private fun countFeatures(program: ProgramNode): Int {
        return program.countries.sumOf { country ->
            country.stations.size +
                    country.geoElements.size +
                    country.municipalities.size +
                    country.layers.sumOf { layer -> layer.elements.size }
        }
    }
}