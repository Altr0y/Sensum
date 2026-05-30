package si.sensum.geodsl

import si.sensum.geodsl.lexer.Lexer
import si.sensum.geodsl.parser.Parser
import java.io.File

fun main() {
    val file = File("test-data/DSL/slovenia.sensum")
    val lexer = Lexer(file.readText(), file.canonicalPath, file.parentFile)

    println("=== TOKENS ===")
    val tokens = lexer.tokenize()
    tokens.forEach { println("  ${it.type.name.padEnd(22)} \"${it.value}\"") }

    println("\n=== AST ===")
    val program = Parser(tokens, lexer).parseProgram()
    println(program)
}