package si.sensum.geodsl.lexer

enum class TokenType {
    // Napake in EOF
    ERR,

    // Literali
    INT,        // 2241, 101
    NUMBER,     // 1.42, -3.5
    STRING,     // "Drava - Melje"
    DATETIME,   // 2026-05-21T10:00:00
    COLOR,      // "#ff0000"

    // Keywords - organizacijski konstrukti
    COUNTRY, REGION, MUNICIPALITY,

    // Keywords - geometrijski konstrukti
    RIVER, LAKE, AREA, FLOOD_ZONE,

    // Keywords - senzorski konstrukti
    STATION, CHANNEL, MEASUREMENT,

    // Keywords - ukazi
    POINT, LINE, POLYGON, RISK, AT,
    KIND, UNIT, VALUE, STATUS, SOURCE,
    THRESHOLD, ABOVE, BELOW,
    DISPLAY, COLOR_KW, LABEL,

    // Vrednosti - channel kind
    WATER_LEVEL, WATER_DEPTH, PRESSURE,
    TEMPERATURE, HUMIDITY, RAINFALL, BATTERY, SIGNAL,

    // Vrednosti - unit
    UNIT_M, UNIT_CM, UNIT_MM, UNIT_HPA,
    UNIT_C, UNIT_PCT, UNIT_V, UNIT_DBM,

    // Vrednosti - status
    STATUS_OK, STATUS_WARNING, STATUS_ERROR,

    // Vrednosti - risk
    RISK_LOW, RISK_MEDIUM, RISK_HIGH, RISK_CRITICAL,

    // Ločila
    LBRACE,   // {
    RBRACE,   // }
    LPAREN,   // (
    RPAREN,   // )
    COMMA,    // ,
    SEMI      // ;
}