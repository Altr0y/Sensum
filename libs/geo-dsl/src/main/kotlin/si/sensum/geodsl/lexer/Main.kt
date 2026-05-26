package si.sensum.geodsl.lexer

import si.sensum.geodsl.parser.Parser

fun main() {
    val parser = Parser("test-data/DSL/geography.sensum")
    val ast = parser.parseProgram()
    if (ast != null) {
        println("AST uspešno zgrajen:")
        println(ast)
    } else {
        println("Napaka pri razčlenjevanju.")
    }
}