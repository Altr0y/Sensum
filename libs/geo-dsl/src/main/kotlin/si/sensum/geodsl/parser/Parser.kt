package si.sensum.geodsl.parser

import si.sensum.geodsl.ast.*
import si.sensum.geodsl.lexer.Token
import si.sensum.geodsl.lexer.TokenType
import si.sensum.geodsl.lexer.Lexer

class Parser(
    private val tokens: List<Token>,
    private val lexer: Lexer? = null
) {
    private var pos = 0

    private val geoTypes = setOf(
        TokenType.RIVER, TokenType.LAKE,
        TokenType.AREA, TokenType.FLOOD_ZONE
    )

    fun parseProgram(): ProgramNode {
        return if (!atEnd() && peek().type in geoTypes) {
            ProgramNode(listOf(parseGeoFile()))
        } else {
            val countries = mutableListOf<CountryNode>()
            while (!atEnd()) countries.add(parseCountry())
            ProgramNode(countries)
        }
    }

    private fun parseGeoFile(): CountryNode {
        val elements = mutableListOf<GeoElement>()
        while (!atEnd() && peek().type in geoTypes) {
            elements.add(parseGeoElement())
        }
        return CountryNode(
            name = "__geo__",
            layers = emptyList(),
            dataSources = emptyList(),
            municipalities = emptyList(),
            stations = emptyList(),
            autoResolves = emptyList(),
            exports = emptyList(),
            geoElements = elements
        )
    }

    private fun parseGeoElement(): GeoElement {
        return when (peek().type) {
            TokenType.RIVER -> parseRiver()
            TokenType.LAKE -> parseLake()
            TokenType.AREA -> parseArea()
            TokenType.FLOOD_ZONE -> parseFloodZone()
            else -> throw ParseException("Nepričakovan geo element '${peek().value}'", peek())
        }
    }

    private fun parseRiver(): RiverNode {
        expect(TokenType.RIVER)
        val name = expect(TokenType.STRING).value
        expect(TokenType.LINE_KW)
        expect(TokenType.LBRACE)
        val points = mutableListOf<PointNode>()
        while (!check(TokenType.RBRACE) && !atEnd()) points.add(parsePoint())
        expect(TokenType.RBRACE)
        return RiverNode(name, points)
    }

    private fun parseLake(): LakeNode {
        expect(TokenType.LAKE)
        val name = expect(TokenType.STRING).value
        expect(TokenType.POLYGON)
        expect(TokenType.LBRACE)
        val points = mutableListOf<PointNode>()
        while (!check(TokenType.RBRACE) && !atEnd()) points.add(parsePoint())
        expect(TokenType.RBRACE)
        return LakeNode(name, points)
    }

    private fun parseArea(): AreaNode {
        expect(TokenType.AREA)
        val name = expect(TokenType.STRING).value
        expect(TokenType.POLYGON)
        expect(TokenType.LBRACE)
        val points = mutableListOf<PointNode>()
        while (!check(TokenType.RBRACE) && !atEnd()) points.add(parsePoint())
        expect(TokenType.RBRACE)
        return AreaNode(name, points)
    }

    private fun parseFloodZone(): FloodZoneNode {
        expect(TokenType.FLOOD_ZONE)
        val name = expect(TokenType.STRING).value
        expect(TokenType.RISK)
        val risk = expect(TokenType.IDENT).value
        expect(TokenType.POLYGON)
        expect(TokenType.LBRACE)
        val points = mutableListOf<PointNode>()
        while (!check(TokenType.RBRACE) && !atEnd()) points.add(parsePoint())
        expect(TokenType.RBRACE)
        return FloodZoneNode(name, risk, points)
    }

    private fun parseCountry(): CountryNode {
        expect(TokenType.COUNTRY)
        val name = expect(TokenType.STRING).value
        expect(TokenType.LBRACE)

        val layers = mutableListOf<LayerNode>()
        val dataSources = mutableListOf<DataSourceNode>()
        val municipalities = mutableListOf<MunicipalityNode>()
        val stations = mutableListOf<StationNode>()
        val autoResolves = mutableListOf<AutoResolveNode>()
        val exports = mutableListOf<ExportNode>()

        while (!check(TokenType.RBRACE) && !atEnd()) {
            when (peek().type) {
                TokenType.LAYER -> layers.add(parseLayer())
                TokenType.DATA_SOURCE -> dataSources.add(parseDataSource())
                TokenType.MUNICIPALITY -> municipalities.add(parseMunicipality())
                TokenType.STATION -> stations.add(parseStation())
                TokenType.AUTO_RESOLVE -> autoResolves.add(parseAutoResolve())
                TokenType.EXPORT -> exports.add(parseExport())
                else -> throw ParseException("Nepričakovan token '${peek().value}'", peek())
            }
        }

        expect(TokenType.RBRACE)
        return CountryNode(name, layers, dataSources, municipalities, stations, autoResolves, exports)
    }

    private fun parseLayer(): LayerNode {
        expect(TokenType.LAYER)
        val name = expect(TokenType.IDENT).value
        expect(TokenType.FROM)
        val path = expect(TokenType.STRING).value
        expect(TokenType.SEMICOLON)
        val elements = lexer?.resolveLayer(path, tokens[pos - 1]) ?: emptyList()
        return LayerNode(name, path, elements)
    }


    private fun parseDataSource(): DataSourceNode {
        expect(TokenType.DATA_SOURCE)
        val name = expect(TokenType.IDENT).value
        expect(TokenType.FROM)
        expect(TokenType.DATABASE)
        expect(TokenType.TABLE)
        val table = expect(TokenType.STRING).value
        expect(TokenType.SEMICOLON)
        return DataSourceNode(name, table)
    }

    private fun parseMunicipality(): MunicipalityNode {
        expect(TokenType.MUNICIPALITY)
        val name = expect(TokenType.STRING).value
        expect(TokenType.POLYGON)
        expect(TokenType.LBRACE)
        val points = mutableListOf<PointNode>()
        while (!check(TokenType.RBRACE) && !atEnd()) points.add(parsePoint())
        expect(TokenType.RBRACE)
        return MunicipalityNode(name, points)
    }

    private fun parsePoint(): PointNode {
        expect(TokenType.POINT)
        expect(TokenType.LPAREN)
        val lon = num()
        expect(TokenType.COMMA)
        val lat = num()
        expect(TokenType.RPAREN)
        expect(TokenType.SEMICOLON)
        return PointNode(lon, lat)
    }

    private fun parseStation(): StationNode {
        expect(TokenType.STATION)
        val id = expect(TokenType.INTEGER).value.toInt()
        val name = expect(TokenType.STRING).value
        expect(TokenType.AT)
        expect(TokenType.LPAREN)
        val lon = num()
        expect(TokenType.COMMA)
        val lat = num()
        expect(TokenType.RPAREN)
        expect(TokenType.LBRACE)
        val channels = mutableListOf<ChannelNode>()
        while (!check(TokenType.RBRACE) && !atEnd()) channels.add(parseChannel())
        expect(TokenType.RBRACE)
        return StationNode(id, name, PointNode(lon, lat), channels)
    }

    private fun parseChannel(): ChannelNode {
        expect(TokenType.CHANNEL)
        val id = expect(TokenType.INTEGER).value.toInt()
        val name = expect(TokenType.STRING).value
        expect(TokenType.KIND)
        val kind = when (advance().type) {
            TokenType.WATER_LEVEL -> ChannelKind.WATER_LEVEL
            TokenType.TEMPERATURE -> ChannelKind.TEMPERATURE
            TokenType.RAINFALL -> ChannelKind.RAINFALL
            TokenType.FLOW_RATE -> ChannelKind.FLOW_RATE
            else -> throw ParseException("Neznan tip kanala '${cur().value}'", cur())
        }
        expect(TokenType.UNIT)
        val unit = advance().value
        expect(TokenType.LBRACE)
        val measurements = mutableListOf<MeasurementNode>()
        while (!check(TokenType.RBRACE) && !atEnd()) measurements.add(parseMeasurement())
        expect(TokenType.RBRACE)
        return ChannelNode(id, name, kind, unit, measurements)
    }

    private fun parseMeasurement(): MeasurementNode {
        expect(TokenType.MEASUREMENT)
        val dt = expect(TokenType.DATETIME).value
        expect(TokenType.VALUE)
        val value = num()
        expect(TokenType.STATUS)
        val status = when (advance().type) {
            TokenType.STATUS_OK -> MeasurementStatus.OK
            TokenType.STATUS_WARNING -> MeasurementStatus.WARNING
            TokenType.STATUS_CRITICAL -> MeasurementStatus.CRITICAL
            TokenType.STATUS_ERROR -> MeasurementStatus.ERROR
            else -> throw ParseException("Neznan status '${cur().value}'", cur())
        }
        expect(TokenType.SEMICOLON)
        return MeasurementNode(dt, value, status)
    }

    private fun parseAutoResolve(): AutoResolveNode {
        expect(TokenType.AUTO_RESOLVE)
        val target = expect(TokenType.IDENT).value
        expect(TokenType.LBRACE)
        val rules = mutableListOf<ResolveRuleNode>()
        while (!check(TokenType.RBRACE) && !atEnd()) {
            rules.add(
                when (peek().type) {
                    TokenType.BELONGS_TO -> {
                        expect(TokenType.BELONGS_TO); expect(TokenType.TYPE)
                        val t = anyWord()
                        expect(TokenType.BY); expect(TokenType.CONTAINS)
                        val req = reqOpt(); expect(TokenType.SEMICOLON)
                        BelongsToRuleNode(t, req)
                    }

                    TokenType.INSIDE -> {
                        expect(TokenType.INSIDE); expect(TokenType.TYPE)
                        val t = anyWord()
                        val req = reqOpt(); expect(TokenType.SEMICOLON)
                        InsideRuleNode(t, req)
                    }

                    TokenType.NEAR -> {
                        expect(TokenType.NEAR); expect(TokenType.TYPE)
                        val t = anyWord()
                        expect(TokenType.WITHIN)
                        val m = expect(TokenType.DISTANCE).value.removeSuffix("m").toInt()
                        val req = reqOpt(); expect(TokenType.SEMICOLON)
                        NearRuleNode(t, m, req)
                    }

                    else -> throw ParseException("Pričakovano pravilo, dobljeno '${peek().value}'", peek())
                }
            )
        }
        expect(TokenType.RBRACE)
        return AutoResolveNode(target, rules)
    }

    private fun parseExport(): ExportNode {
        expect(TokenType.EXPORT)
        val format = when (advance().type) {
            TokenType.FORMAT_GEOJSON -> ExportFormat.GEOJSON
            TokenType.FORMAT_CSV -> ExportFormat.CSV
            TokenType.FORMAT_JSON -> ExportFormat.JSON
            else -> throw ParseException("Neznan format '${cur().value}'", cur())
        }
        val path = expect(TokenType.STRING).value
        expect(TokenType.LBRACE)
        val options = mutableListOf<ExportOptionNode>()
        while (!check(TokenType.RBRACE) && !atEnd()) {
            if (!check(TokenType.INCLUDE))
                throw ParseException("Pričakovano 'include', dobljeno '${peek().value}'", peek())
            advance()
            options.add(
                when (peek().type) {
                    TokenType.STATION -> {
                        advance(); expect(TokenType.GEOMETRY); advance()
                        expect(TokenType.SEMICOLON); StationGeometryOption("point")
                    }

                    TokenType.SPATIAL_REFS -> {
                        advance(); expect(TokenType.SEMICOLON); SpatialRefsOption
                    }

                    TokenType.LATEST_MEASUREMENTS -> {
                        advance(); expect(TokenType.SEMICOLON); LatestMeasurementsOption
                    }

                    TokenType.IDENT -> {
                        val n = advance().value; expect(TokenType.SEMICOLON); RawIdentOption(n)
                    }

                    else -> throw ParseException("Neznana export opcija '${peek().value}'", peek())
                }
            )
        }
        expect(TokenType.RBRACE)
        return ExportNode(format, path, options)
    }

    private fun num(): Double {
        val t = peek()
        return when (t.type) {
            TokenType.NUMBER, TokenType.INTEGER -> {
                advance(); t.value.toDouble()
            }

            else -> throw ParseException("Pričakovano število, dobljeno '${t.value}'", t)
        }
    }

    private fun reqOpt(): Boolean = when (advance().type) {
        TokenType.REQUIRED -> true
        TokenType.OPTIONAL -> false
        else -> throw ParseException("Pričakovano 'required' ali 'optional', dobljeno '${cur().value}'", cur())
    }

    private fun expect(type: TokenType): Token {
        if (check(type)) return advance()
        throw ParseException("Pričakovan $type, dobljeno ${peek().type} (\"${peek().value}\")", peek())
    }

    private fun anyWord(): String = advance().value
    private fun check(type: TokenType) = !atEnd() && peek().type == type
    private fun peek() = tokens[pos]
    private fun cur() = tokens[pos - 1]
    private fun advance() = tokens[pos++]
    private fun atEnd() = pos >= tokens.size || tokens[pos].type == TokenType.EOF
}

class ParseException(message: String, token: Token) :
    Exception("$message [${token.file}:${token.line}:${token.column}]")