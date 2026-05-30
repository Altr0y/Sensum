package si.sensum.geodsl.lexer

import java.io.File
import si.sensum.geodsl.ast.GeoElement
import si.sensum.geodsl.parser.Parser

const val MAX_STATE = 16
const val NO_EDGE = -1

class Lexer(
    private val source: String,
    private val fileName: String = "<input>",
    private val baseDir: File? = null,
    private val visited: MutableSet<String> = mutableSetOf()
) {
    private var pos = 0
    private var line = 1
    private var col = 1

    private val automata = Array(MAX_STATE) { IntArray(256) { NO_EDGE } }
    private val finite = Array(MAX_STATE) { TokenType.UNKNOWN }

    companion object {
        val KEYWORDS = mapOf(
            "include" to TokenType.INCLUDE,
            "country" to TokenType.COUNTRY,
            "layer" to TokenType.LAYER,
            "from" to TokenType.FROM,
            "data_source" to TokenType.DATA_SOURCE,
            "database" to TokenType.DATABASE,
            "table" to TokenType.TABLE,
            "municipality" to TokenType.MUNICIPALITY,
            "polygon" to TokenType.POLYGON,
            "station" to TokenType.STATION,
            "at" to TokenType.AT,
            "channel" to TokenType.CHANNEL,
            "kind" to TokenType.KIND,
            "unit" to TokenType.UNIT,
            "measurement" to TokenType.MEASUREMENT,
            "value" to TokenType.VALUE,
            "status" to TokenType.STATUS,
            "auto_resolve" to TokenType.AUTO_RESOLVE,
            "belongs_to" to TokenType.BELONGS_TO,
            "type" to TokenType.TYPE,
            "by" to TokenType.BY,
            "contains" to TokenType.CONTAINS,
            "inside" to TokenType.INSIDE,
            "near" to TokenType.NEAR,
            "within" to TokenType.WITHIN,
            "export" to TokenType.EXPORT,
            "required" to TokenType.REQUIRED,
            "optional" to TokenType.OPTIONAL,
            "water_level" to TokenType.WATER_LEVEL,
            "temperature" to TokenType.TEMPERATURE,
            "rainfall" to TokenType.RAINFALL,
            "flow_rate" to TokenType.FLOW_RATE,
            "ok" to TokenType.STATUS_OK,
            "warning" to TokenType.STATUS_WARNING,
            "critical" to TokenType.STATUS_CRITICAL,
            "error" to TokenType.STATUS_ERROR,
            "geojson" to TokenType.FORMAT_GEOJSON,
            "csv" to TokenType.FORMAT_CSV,
            "json" to TokenType.FORMAT_JSON,
            "geometry" to TokenType.GEOMETRY,
            "point" to TokenType.POINT,
            "spatial_refs" to TokenType.SPATIAL_REFS,
            "latest_measurements" to TokenType.LATEST_MEASUREMENTS,
            "river" to TokenType.RIVER,
            "lake" to TokenType.LAKE,
            "area" to TokenType.AREA,
            "flood_zone" to TokenType.FLOOD_ZONE,
            "risk" to TokenType.RISK,
            "line" to TokenType.LINE_KW,
        )

        private val DATETIME_REGEX = Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}""")
    }

    init {
        // Stanje 1: INT
        for (c in '0'..'9') automata[0][c.code] = 1
        for (c in '0'..'9') automata[1][c.code] = 1
        finite[1] = TokenType.INTEGER

        // Stanje 2: NUM decimalna pika
        automata[1]['.'.code] = 2
        finite[2] = TokenType.UNKNOWN

        // Stanje 3: NUMBER
        for (c in '0'..'9') automata[2][c.code] = 3
        for (c in '0'..'9') automata[3][c.code] = 3
        finite[3] = TokenType.NUMBER

        // Stanje 4: IDENT/KW
        for (c in 'a'..'z') automata[0][c.code] = 4
        for (c in 'A'..'Z') automata[0][c.code] = 4
        automata[0]['_'.code] = 4
        for (c in 'a'..'z') automata[4][c.code] = 4
        for (c in 'A'..'Z') automata[4][c.code] = 4
        for (c in '0'..'9') automata[4][c.code] = 4
        automata[4]['_'.code] = 4
        finite[4] = TokenType.IDENT  // resolve v nextToken()

        // Stanje 5: NEG
        automata[0]['-'.code] = 5
        finite[5] = TokenType.UNKNOWN

        // Stanje 6: NEG INT
        for (c in '0'..'9') automata[5][c.code] = 6
        for (c in '0'..'9') automata[6][c.code] = 6
        finite[6] = TokenType.NUMBER

        // Stanje 7: NEG NUM decimalna pika
        automata[6]['.'.code] = 7
        finite[7] = TokenType.UNKNOWN

        // Stanje 8: NEG NUMBER
        for (c in '0'..'9') automata[7][c.code] = 8
        for (c in '0'..'9') automata[8][c.code] = 8
        finite[8] = TokenType.NUMBER

        // Stanje 9: STR v teku
        automata[0]['"'.code] = 9
        for (i in 0..255) automata[9][i] = 9
        automata[9]['"'.code] = 10
        automata[9]['\\'.code] = 11
        finite[9] = TokenType.UNKNOWN  // nedokončan niz

        // Stanje 10: STRING končno
        finite[10] = TokenType.STRING

        // Stanje 11: STR escape
        for (i in 0..255) automata[11][i] = 9
        finite[11] = TokenType.UNKNOWN

        // Stanje 12: DATETIME — iz stanja 1 ko pride '-'
        automata[1]['-'.code] = 12
        for (c in '0'..'9') automata[12][c.code] = 12
        automata[12]['-'.code] = 12
        automata[12]['T'.code] = 12
        automata[12][':'.code] = 12
        finite[12] = TokenType.DATETIME

        // Stanje 13: DISTANCE — iz stanja 1 ali 6 ko pride 'm'
        automata[1]['m'.code] = 13
        automata[6]['m'.code] = 13
        finite[13] = TokenType.DISTANCE

        // Stanje 14: COMMENT — '//' do konca vrstice
        automata[0]['/'.code] = 14
        for (i in 0..255) automata[14][i] = 14
        automata[14]['\n'.code] = NO_EDGE  // EXIT ob novem vrstici
        finite[14] = TokenType.UNKNOWN     // komentarji se preskočijo

        // Stanje 15: ločila
        automata[0]['{'.code] = 15
        automata[0]['}'.code] = 15
        automata[0]['('.code] = 15
        automata[0][')'.code] = 15
        automata[0][','.code] = 15
        automata[0][';'.code] = 15
        finite[15] = TokenType.UNKNOWN  // resolve v nextToken()
    }

    fun tokenize(): List<Token> {
        visited.add(fileName)
        val tokens = mutableListOf<Token>()

        var tok = nextToken()
        while (tok.type != TokenType.EOF) {
            if (tok.type == TokenType.INCLUDE) {
                val nextTok = nextToken()
                if (nextTok.type == TokenType.STRING) {
                    val semi = nextToken()
                    if (semi.type != TokenType.SEMICOLON)
                        throw LexerException("Pričakovan ';' po include", semi)
                    tokens.addAll(resolveInclude(nextTok.value, nextTok))
                } else {
                    tokens.add(tok)
                    tokens.add(nextTok)
                }
            } else {
                tokens.add(tok)
            }
            tok = nextToken()
        }

        tokens.add(Token(TokenType.EOF, "", line, col, fileName))
        return tokens
    }

    private fun resolveInclude(relativePath: String, origin: Token): List<Token> {
        val file = (baseDir ?: File(".")).resolve(relativePath).canonicalFile
        if (!file.exists())
            throw LexerException("Datoteka ne obstaja: ${file.absolutePath}", origin)
        if (file.canonicalPath in visited)
            throw LexerException("Krožna odvisnost: ${file.canonicalPath}", origin)
        return Lexer(file.readText(), file.canonicalPath, file.parentFile, visited)
            .tokenize()
            .filter { it.type != TokenType.EOF }
    }

    fun resolveLayer(path: String, baseToken: Token): List<GeoElement> {
        if (!path.endsWith(".sensum")) return emptyList()
        val file = (baseDir ?: File(".")).resolve(path).canonicalFile
        if (!file.exists()) return emptyList()
        val tokens = Lexer(file.readText(), file.canonicalPath, file.parentFile, visited)
            .tokenize()
            .filter { it.type != TokenType.EOF }
        return Parser(tokens).parseProgram()
            .countries.firstOrNull()?.geoElements ?: emptyList()
    }

    private fun currentChar(): Char =
        if (pos < source.length) source[pos] else '\u0000'

    private fun advance(): Char {
        val c = source[pos]
        if (c == '\n') {
            line++; col = 1
        } else col++
        pos++
        return c
    }

    private fun nextToken(): Token {
        // preskoči whitespace
        while (pos < source.length &&
            (currentChar() == ' ' || currentChar() == '\n' ||
                    currentChar() == '\t' || currentChar() == '\r')
        ) {
            advance()
        }

        if (pos >= source.length)
            return Token(TokenType.EOF, "", line, col, fileName)

        var state = 0
        var lexem = ""
        val startLine = line
        val startCol = col
        var lastChar = ' '

        while (true) {
            val c = currentChar()
            if (c == '\u0000') break
            val nextState = automata[state][c.code]
            if (nextState == NO_EDGE) break
            lastChar = c
            state = nextState
            lexem += advance()
        }

        if (lexem.isEmpty()) {
            val unknown = advance()
            return Token(TokenType.UNKNOWN, unknown.toString(), startLine, startCol, fileName)
        }

        var tokenType = finite[state]

        when (state) {
            4 -> {
                // IDENT resolve — keywords
                tokenType = KEYWORDS[lexem] ?: TokenType.IDENT
            }

            10 -> {
                // STRING — odstrani narekovaje
                lexem = lexem.removeSurrounding("\"")
                tokenType = TokenType.STRING
            }

            12 -> {
                // DATETIME validacija
                if (!DATETIME_REGEX.matches(lexem))
                    throw LexerException("Neveljaven datetime: '$lexem'", null)
                tokenType = TokenType.DATETIME
            }

            14 -> {
                // COMMENT — preskoči in vrni naslednji token
                return nextToken()
            }

            15 -> {
                // ločila resolve
                tokenType = when (lastChar) {
                    '{' -> TokenType.LBRACE
                    '}' -> TokenType.RBRACE
                    '(' -> TokenType.LPAREN
                    ')' -> TokenType.RPAREN
                    ',' -> TokenType.COMMA
                    ';' -> TokenType.SEMICOLON
                    else -> TokenType.UNKNOWN
                }
            }
        }

        return Token(tokenType, lexem, startLine, startCol, fileName)
    }


}



class LexerException(message: String, val token: Token?) :
    Exception(if (token != null) "$message [${token.file}:${token.line}:${token.column}]" else message)