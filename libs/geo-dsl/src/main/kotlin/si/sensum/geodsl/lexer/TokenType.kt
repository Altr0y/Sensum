package si.sensum.geodsl.lexer

enum class TokenType {
    // Ključne besede — struktura
    INCLUDE,
    COUNTRY,
    LAYER,
    FROM,
    DATA_SOURCE,
    DATABASE,
    TABLE,
    MUNICIPALITY,
    POLYGON,
    STATION,
    AT,
    CHANNEL,
    KIND,
    UNIT,
    MEASUREMENT,
    VALUE,
    STATUS,
    AUTO_RESOLVE,
    BELONGS_TO,
    TYPE,
    BY,
    CONTAINS,
    INSIDE,
    NEAR,
    WITHIN,
    EXPORT,
    INCLUDE_KW,

    // Geografski konstrukti
    RIVER,
    LAKE,
    REGION,
    FLOOD_ZONE,
    RISK,
    LINE_KW,

    // Ključne besede — vrednosti
    REQUIRED,
    OPTIONAL,

    // Tipi kanalov
    WATER_LEVEL,
    TEMPERATURE,
    RAINFALL,
    FLOW_RATE,

    // Statusne vrednosti
    STATUS_OK,
    STATUS_WARNING,
    STATUS_CRITICAL,
    STATUS_ERROR,

    // Export formati
    FORMAT_GEOJSON,
    FORMAT_CSV,
    FORMAT_JSON,

    // Export opcije
    GEOMETRY,
    POINT_KW,
    SPATIAL_REFS,
    LATEST_MEASUREMENTS,

    // Geografski primitivi
    POINT,

    // Literali
    STRING,
    NUMBER,
    INTEGER,
    DATETIME,
    DISTANCE,

    // Identifikator
    IDENT,

    // Ločila
    LBRACE,
    RBRACE,
    LPAREN,
    RPAREN,
    COMMA,
    SEMICOLON,

    // Posebni
    EOF, UNKNOWN
}