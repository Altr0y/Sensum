package si.sensum.geodsl.lexer

import java.io.File

const val MAX_STATE = 15
const val START_STATE = 0
const val NO_EDGE = -1

class Lexer(val filePath: String) {
    var row: Int = 1
    var column: Int = 1

    val automata = Array(MAX_STATE) { IntArray(256) { NO_EDGE } }
    val finite = Array(MAX_STATE) { TokenType.ERR }

    val reader = File(filePath).bufferedReader()
    var currentChar: Char = reader.read().toChar()

    val keywords = mapOf(
        // Organizacijski konstrukti
        "country" to TokenType.COUNTRY,
        "region" to TokenType.REGION,
        "municipality" to TokenType.MUNICIPALITY,
        // Geometrijski konstrukti
        "river" to TokenType.RIVER,
        "lake" to TokenType.LAKE,
        "area" to TokenType.AREA,
        "flood_zone" to TokenType.FLOOD_ZONE,
        // Senzorski konstrukti
        "station" to TokenType.STATION,
        "channel" to TokenType.CHANNEL,
        "measurement" to TokenType.MEASUREMENT,
        // Ukazi
        "point" to TokenType.POINT,
        "line" to TokenType.LINE,
        "polygon" to TokenType.POLYGON,
        "at" to TokenType.AT,
        "kind" to TokenType.KIND,
        "unit" to TokenType.UNIT,
        "value" to TokenType.VALUE,
        "status" to TokenType.STATUS,
        "source" to TokenType.SOURCE,
        "threshold" to TokenType.THRESHOLD,
        "above" to TokenType.ABOVE,
        "below" to TokenType.BELOW,
        "display" to TokenType.DISPLAY,
        "color" to TokenType.COLOR_KW,
        "label" to TokenType.LABEL,
        "risk" to TokenType.RISK,
        // Channel kind
        "water_level" to TokenType.WATER_LEVEL,
        "water_depth" to TokenType.WATER_DEPTH,
        "pressure" to TokenType.PRESSURE,
        "temperature" to TokenType.TEMPERATURE,
        "humidity" to TokenType.HUMIDITY,
        "rainfall" to TokenType.RAINFALL,
        "battery" to TokenType.BATTERY,
        "signal" to TokenType.SIGNAL,
        // Unit
        "m" to TokenType.UNIT_M,
        "cm" to TokenType.UNIT_CM,
        "mm" to TokenType.UNIT_MM,
        "hPa" to TokenType.UNIT_HPA,
        "C" to TokenType.UNIT_C,
        "V" to TokenType.UNIT_V,
        "dBm" to TokenType.UNIT_DBM,
        // Status
        "ok" to TokenType.STATUS_OK,
        "warning" to TokenType.STATUS_WARNING,
        "error" to TokenType.STATUS_ERROR,
        // Risk
        "low" to TokenType.RISK_LOW,
        "medium" to TokenType.RISK_MEDIUM,
        "high" to TokenType.RISK_HIGH,
        "critical" to TokenType.RISK_CRITICAL,
    )

    init {
        // Stanje 1: INT
        for (c in '0'..'9') automata[0][c.code] = 1
        for (c in '0'..'9') automata[1][c.code] = 1
        finite[1] = TokenType.INT

        // Stanje 2: NUM decimalna pika
        automata[1]['.'.code] = 2
        finite[2] = TokenType.ERR  // samo pika brez cifre = napaka

        // Stanje 3: NUMBER
        for (c in '0'..'9') automata[2][c.code] = 3
        for (c in '0'..'9') automata[3][c.code] = 3
        finite[3] = TokenType.NUMBER

        // Stanje 4: IDENT
        for (c in 'a'..'z') automata[0][c.code] = 4
        for (c in 'A'..'Z') automata[0][c.code] = 4
        automata[0]['_'.code] = 4
        for (c in 'a'..'z') automata[4][c.code] = 4
        for (c in 'A'..'Z') automata[4][c.code] = 4
        for (c in '0'..'9') automata[4][c.code] = 4
        automata[4]['_'.code] = 4
        automata[4]['-'.code] = 4
        finite[4] = TokenType.ERR  // resolve v nextToken()

        // Stanje 5: KW/DT — resolve, ni prehoda iz avtomata

        // Stanje 6: STR v teku
        automata[0]['"'.code] = 6
        for (i in 0..255) automata[6][i] = 6
        automata[6]['"'.code] = 8
        automata[6]['\\'.code] = 7
        finite[6] = TokenType.ERR  // nedokončan niz

        // Stanje 7: STR escape
        for (i in 0..255) automata[7][i] = 6
        finite[7] = TokenType.ERR

        // Stanje 8: STRING končno
        finite[8] = TokenType.STRING

        // Stanje 9: COLOR
        automata[0]['#'.code] = 9
        for (c in '0'..'9') automata[9][c.code] = 9
        for (c in 'a'..'f') automata[9][c.code] = 9
        for (c in 'A'..'F') automata[9][c.code] = 9
        finite[9] = TokenType.COLOR

        // Stanje 10: NEG
        automata[0]['-'.code] = 10
        finite[10] = TokenType.ERR  // samo minus = napaka

        // Stanje 11: NEG INT
        for (c in '0'..'9') automata[10][c.code] = 11
        for (c in '0'..'9') automata[11][c.code] = 11
        finite[11] = TokenType.NUMBER

        // Stanje 12: NEG NUM
        automata[11]['.'.code] = 12
        for (c in '0'..'9') automata[12][c.code] = 12
        finite[12] = TokenType.NUMBER

        // Stanje 13: LBRACE, RBRACE
        automata[0]['{'.code] = 13
        automata[0]['}'.code] = 13
        finite[13] = TokenType.ERR  // resolve v nextToken()

        // Stanje 14: LPAREN, RPAREN, COMMA, SEMI, PCT
        automata[0]['('.code] = 14
        automata[0][')'.code] = 14
        automata[0][','.code] = 14
        automata[0][';'.code] = 14
        automata[0]['%'.code] = 14
        finite[14] = TokenType.ERR  // resolve v nextToken()
    }

    fun nextChar(): Char {
        val c = currentChar
        val next = reader.read()
        currentChar = if (next == -1) '\u0000' else next.toChar()
        return c
    }

    fun nextToken(): Token {
        // Preskoči whitespace
        while (currentChar == ' ' || currentChar == '\n' ||
            currentChar == '\t' || currentChar == '\r'
        ) {
            if (currentChar == '\n') {
                row++; column = 1
            } else column++
            nextChar()
        }

        // EOF
        if (currentChar == '\u0000') return Token(
            lexem = "", type = TokenType.ERR,
            row = row, column = column, eof = true
        )

        var state = START_STATE
        var lexem = ""
        val startRow = row
        val startColumn = column
        var lastChar = ' '

        while (true) {
            val nextState = automata[state][currentChar.code]
            if (nextState == NO_EDGE) break
            lastChar = currentChar
            state = nextState
            lexem += nextChar()
            column++
        }

        if (lexem.isEmpty()) {
            val unknown = currentChar
            nextChar()
            column++
            return Token(
                lexem = unknown.toString(),
                type = TokenType.ERR,
                row = startRow, column = startColumn
            )
        }

        var tokenType = finite[state]

        when (state) {
            4 -> {
                // IDENT resolve — keywords ali DATETIME
                val datetimeRegex = Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}""")
                tokenType = if (datetimeRegex.matches(lexem)) {
                    TokenType.DATETIME
                } else {
                    keywords[lexem] ?: TokenType.ERR
                }
            }

            8 -> {
                // STRING — odstrani narekovaje
                lexem = lexem.removeSurrounding("\"")
                tokenType = TokenType.STRING
            }

            13 -> {
                // LBRACE ali RBRACE
                tokenType = when (lastChar) {
                    '{' -> TokenType.LBRACE
                    '}' -> TokenType.RBRACE
                    else -> TokenType.ERR
                }
            }

            14 -> {
                // LPAREN, RPAREN, COMMA, SEMI, UNIT_PCT
                tokenType = when (lastChar) {
                    '(' -> TokenType.LPAREN
                    ')' -> TokenType.RPAREN
                    ',' -> TokenType.COMMA
                    ';' -> TokenType.SEMI
                    '%' -> TokenType.UNIT_PCT
                    else -> TokenType.ERR
                }
            }
        }

        return Token(
            lexem = lexem, type = tokenType,
            row = startRow, column = startColumn
        )
    }
}