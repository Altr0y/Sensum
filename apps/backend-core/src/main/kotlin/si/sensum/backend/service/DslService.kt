package si.sensum.backend.service

import si.sensum.geodsl.GeoDslProcessor
import si.sensum.geodsl.ast.ProgramNode
import si.sensum.geodsl.export.export
import si.sensum.geodsl.lexer.LexerException
import si.sensum.geodsl.parser.ParseException
import si.sensum.shared.models.dsl.DslProcessResult

internal class DslService {

    fun processSource(source: String): DslProcessResult {
        val program = try {
            GeoDslProcessor.parseString(source)
        } catch (e: LexerException) {
            throw IllegalArgumentException("DSL lexer error: ${e.message}", e)
        } catch (e: ParseException) {
            throw IllegalArgumentException("DSL parse error: ${e.message}", e)
        }

        val geoJson = export(program)
        return DslProcessResult(
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
