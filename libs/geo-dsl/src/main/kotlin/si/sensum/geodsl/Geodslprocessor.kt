package si.sensum.geodsl

import si.sensum.geodsl.ast.ProgramNode
import si.sensum.geodsl.lexer.Lexer
import si.sensum.geodsl.parser.Parser
import java.io.File

object GeoDslProcessor {
    fun parseFile(file: File): ProgramNode {
        require(file.exists()) { "Datoteka ne obstaja: ${file.absolutePath}" }
        val tokens = Lexer(file.readText(), file.canonicalPath, file.parentFile).tokenize()
        return Parser(tokens).parseProgram()
    }

    fun parseString(source: String, name: String = "<string>"): ProgramNode {
        val tokens = Lexer(source, name).tokenize()
        return Parser(tokens).parseProgram()
    }
}