package si.sensum.geodsl.lexer

data class Token(
    val lexem: String = "",
    val type: TokenType,
    val row: Int,
    val column: Int,
    val eof: Boolean = false
) {
    fun printToken() {
        if (eof) println("EOF")
        else println("($type) \"$lexem\" [$row:$column]")
    }
}
