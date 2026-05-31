package si.sensum.geodsl.lexer

/**
 * En token iz vhodnega toka.
 *
 * @param type    tip tokena
 * @param value   izvirni leksem iz vhodne datoteke
 * @param line    vrstica (1-based) za sporočila o napakah
 * @param column  stolpec (1-based) za sporočila o napakah
 * @param file    pot do izvorne datoteke (za rekurzivne include)
 */
data class Token(
    val type: TokenType,
    val value: String,
    val line: Int,
    val column: Int,
    val file: String = "<input>"
) {
    override fun toString(): String = "Token($type, \"$value\", $file:$line:$column)"
}