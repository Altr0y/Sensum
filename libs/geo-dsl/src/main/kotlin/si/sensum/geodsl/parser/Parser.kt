package si.sensum.geodsl.parser

import si.sensum.geodsl.ast.*
import si.sensum.geodsl.lexer.Lexer
import si.sensum.geodsl.lexer.Token
import si.sensum.geodsl.lexer.TokenType

class Parser(filePath: String) {
    val lexer = Lexer(filePath)
    var currentToken: Token = lexer.nextToken()

    fun eat(type: TokenType): Boolean {
        return if (currentToken.type == type) {
            currentToken = lexer.nextToken()
            true
        } else {
            println("ERR [${currentToken.row}:${currentToken.column}]: expected($type) got(${currentToken.type}) -> \"${currentToken.lexem}\"")
            false
        }
    }

    // Program ::= Country EOF
    fun parseProgram(): CountryNode? {
        val country = parseCountry() ?: return null
        if (!currentToken.eof) {
            println("ERR [${currentToken.row}:${currentToken.column}]: unexpected token after end of program")
            return null
        }
        return country
    }

    // Country ::= "country" STRING "{" RegionList "}"
    fun parseCountry(): CountryNode? {
        if (!eat(TokenType.COUNTRY)) return null
        val name = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.LBRACE)) return null
        val regions = parseRegionList()
        if (!eat(TokenType.RBRACE)) return null
        return CountryNode(name = name, regions = regions)
    }

    // RegionList ::= Region RegionList | ε
    fun parseRegionList(): List<RegionNode> {
        val regions = mutableListOf<RegionNode>()
        while (currentToken.type == TokenType.REGION) {
            val region = parseRegion() ?: break
            regions.add(region)
        }
        return regions
    }

    // Region ::= "region" STRING "{" MunicipalityList "}"
    fun parseRegion(): RegionNode? {
        if (!eat(TokenType.REGION)) return null
        val name = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.LBRACE)) return null
        val municipalities = parseMunicipalityList()
        if (!eat(TokenType.RBRACE)) return null
        return RegionNode(name = name, municipalities = municipalities)
    }

    // MunicipalityList ::= Municipality MunicipalityList | ε
    fun parseMunicipalityList(): List<MunicipalityNode> {
        val municipalities = mutableListOf<MunicipalityNode>()
        while (currentToken.type == TokenType.MUNICIPALITY) {
            val municipality = parseMunicipality() ?: break
            municipalities.add(municipality)
        }
        return municipalities
    }

    // Municipality ::= "municipality" STRING "{" MunicipalityElementList "}"
    fun parseMunicipality(): MunicipalityNode? {
        if (!eat(TokenType.MUNICIPALITY)) return null
        val name = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.LBRACE)) return null
        val elements = parseMunicipalityElementList()
        if (!eat(TokenType.RBRACE)) return null
        return MunicipalityNode(name = name, elements = elements)
    }

    // MunicipalityElementList ::= MunicipalityElement MunicipalityElementList | ε
    fun parseMunicipalityElementList(): List<MunicipalityElement> {
        val elements = mutableListOf<MunicipalityElement>()
        val startTypes = setOf(
            TokenType.RIVER, TokenType.LAKE, TokenType.AREA,
            TokenType.FLOOD_ZONE, TokenType.STATION
        )
        while (currentToken.type in startTypes) {
            val element = parseMunicipalityElement() ?: break
            elements.add(element)
        }
        return elements
    }

    // MunicipalityElement ::= River | Lake | Area | FloodZone | Station
    fun parseMunicipalityElement(): MunicipalityElement? {
        return when (currentToken.type) {
            TokenType.RIVER -> parseRiver()
            TokenType.LAKE -> parseLake()
            TokenType.AREA -> parseArea()
            TokenType.FLOOD_ZONE -> parseFloodZone()
            TokenType.STATION -> parseStation()
            else -> {
                println("ERR [${currentToken.row}:${currentToken.column}]: unexpected token(${currentToken.type})")
                null
            }
        }
    }

    // River ::= "river" STRING "line" "{" GeoCommandList "}"
    fun parseRiver(): RiverNode? {
        if (!eat(TokenType.RIVER)) return null
        val name = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.LINE)) return null
        if (!eat(TokenType.LBRACE)) return null
        val commands = parseGeoCommandList()
        if (!eat(TokenType.RBRACE)) return null
        return RiverNode(name = name, commands = commands)
    }

    // Lake ::= "lake" STRING "polygon" "{" GeoCommandList "}"
    fun parseLake(): LakeNode? {
        if (!eat(TokenType.LAKE)) return null
        val name = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.POLYGON)) return null
        if (!eat(TokenType.LBRACE)) return null
        val commands = parseGeoCommandList()
        if (!eat(TokenType.RBRACE)) return null
        return LakeNode(name = name, commands = commands)
    }

    // Area ::= "area" STRING "polygon" "{" GeoCommandList "}"
    fun parseArea(): AreaNode? {
        if (!eat(TokenType.AREA)) return null
        val name = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.POLYGON)) return null
        if (!eat(TokenType.LBRACE)) return null
        val commands = parseGeoCommandList()
        if (!eat(TokenType.RBRACE)) return null
        return AreaNode(name = name, commands = commands)
    }

    // FloodZone ::= "flood_zone" STRING "risk" RiskValue "polygon" "{" GeoCommandList "}"
    fun parseFloodZone(): FloodZoneNode? {
        if (!eat(TokenType.FLOOD_ZONE)) return null
        val name = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.RISK)) return null
        val risk = currentToken.lexem
        if (!eatRiskValue()) return null
        if (!eat(TokenType.POLYGON)) return null
        if (!eat(TokenType.LBRACE)) return null
        val commands = parseGeoCommandList()
        if (!eat(TokenType.RBRACE)) return null
        return FloodZoneNode(name = name, risk = risk, commands = commands)
    }

    fun eatRiskValue(): Boolean {
        return when (currentToken.type) {
            TokenType.RISK_LOW, TokenType.RISK_MEDIUM,
            TokenType.RISK_HIGH, TokenType.RISK_CRITICAL -> {
                currentToken = lexer.nextToken()
                true
            }

            else -> {
                println("ERR [${currentToken.row}:${currentToken.column}]: expected risk value (low/medium/high/critical) got(${currentToken.type})")
                false
            }
        }
    }

    // Station ::= "station" INT STRING "at" Coordinate "{" StationBodyList "}"
    fun parseStation(): StationNode? {
        if (!eat(TokenType.STATION)) return null
        val stationId = currentToken.lexem.toIntOrNull() ?: run {
            println("ERR [${currentToken.row}:${currentToken.column}]: expected INT for stationId")
            return null
        }
        if (!eat(TokenType.INT)) return null
        val name = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.AT)) return null
        val coordinate = parseCoordinate() ?: return null
        if (!eat(TokenType.LBRACE)) return null
        val commands = mutableListOf<StationCommand>()
        val channels = mutableListOf<ChannelNode>()
        while (currentToken.type != TokenType.RBRACE && !currentToken.eof) {
            when (currentToken.type) {
                TokenType.CHANNEL -> {
                    val channel = parseChannel() ?: break
                    channels.add(channel)
                }

                TokenType.SOURCE -> {
                    val cmd = parseStationSource() ?: break
                    commands.add(cmd)
                }

                TokenType.DISPLAY -> {
                    val cmd = parseStationDisplay() ?: break
                    commands.add(cmd)
                }

                TokenType.LABEL -> {
                    val cmd = parseStationLabel() ?: break
                    commands.add(cmd)
                }

                else -> {
                    println("ERR [${currentToken.row}:${currentToken.column}]: unexpected token in station(${currentToken.type})")
                    break
                }
            }
        }
        if (!eat(TokenType.RBRACE)) return null
        return StationNode(
            stationId = stationId, name = name,
            coordinate = coordinate, commands = commands, channels = channels
        )
    }

    // GeoCommandList ::= GeoCommand GeoCommandList | ε
    fun parseGeoCommandList(): List<GeoCommand> {
        val commands = mutableListOf<GeoCommand>()
        val startTypes = setOf(
            TokenType.POINT, TokenType.DISPLAY,
            TokenType.LABEL, TokenType.SOURCE
        )
        while (currentToken.type in startTypes) {
            val cmd = parseGeoCommand() ?: break
            commands.add(cmd)
        }
        return commands
    }

    // GeoCommand ::= Point | DisplayCommand | LabelCommand | SourceCommand
    fun parseGeoCommand(): GeoCommand? {
        return when (currentToken.type) {
            TokenType.POINT -> parsePoint()
            TokenType.DISPLAY -> parseDisplay()
            TokenType.LABEL -> parseLabel()
            TokenType.SOURCE -> parseSource()
            else -> {
                println("ERR [${currentToken.row}:${currentToken.column}]: unexpected geo command(${currentToken.type})")
                null
            }
        }
    }

    // Point ::= "point" "(" NUMBER "," NUMBER ")" ";"
    fun parsePoint(): PointCommand? {
        if (!eat(TokenType.POINT)) return null
        val coord = parseCoordinate() ?: return null
        if (!eat(TokenType.SEMI)) return null
        return PointCommand(coordinate = coord)
    }

    // Coordinate ::= "(" NUMBER "," NUMBER ")"
    fun parseCoordinate(): Coordinate? {
        if (!eat(TokenType.LPAREN)) return null
        val lon = currentToken.lexem.toDoubleOrNull() ?: run {
            println("ERR [${currentToken.row}:${currentToken.column}]: expected NUMBER for longitude")
            return null
        }
        if (!eat(TokenType.NUMBER)) return null
        if (!eat(TokenType.COMMA)) return null
        val lat = currentToken.lexem.toDoubleOrNull() ?: run {
            println("ERR [${currentToken.row}:${currentToken.column}]: expected NUMBER for latitude")
            return null
        }
        if (!eat(TokenType.NUMBER)) return null
        if (!eat(TokenType.RPAREN)) return null
        return Coordinate(longitude = lon, latitude = lat)
    }

    // DisplayCommand ::= "display" "color" COLOR ";"
    fun parseDisplay(): DisplayCommand? {
        if (!eat(TokenType.DISPLAY)) return null
        if (!eat(TokenType.COLOR_KW)) return null
        val color = currentToken.lexem
        if (!eat(TokenType.COLOR)) return null
        if (!eat(TokenType.SEMI)) return null
        return DisplayCommand(color = color)
    }

    fun parseLabel(): LabelCommand? {
        if (!eat(TokenType.LABEL)) return null
        val label = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.SEMI)) return null
        return LabelCommand(label = label)
    }

    fun parseSource(): SourceCommand? {
        if (!eat(TokenType.SOURCE)) return null
        val source = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.SEMI)) return null
        return SourceCommand(source = source)
    }

    fun parseStationSource(): StationSourceCommand? {
        if (!eat(TokenType.SOURCE)) return null
        val source = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.SEMI)) return null
        return StationSourceCommand(source = source)
    }

    fun parseStationDisplay(): StationDisplayCommand? {
        if (!eat(TokenType.DISPLAY)) return null
        if (!eat(TokenType.COLOR_KW)) return null
        val color = currentToken.lexem
        if (!eat(TokenType.COLOR)) return null
        if (!eat(TokenType.SEMI)) return null
        return StationDisplayCommand(color = color)
    }

    fun parseStationLabel(): StationLabelCommand? {
        if (!eat(TokenType.LABEL)) return null
        val label = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.SEMI)) return null
        return StationLabelCommand(label = label)
    }

    // Channel ::= "channel" INT STRING "kind" ChannelKind "unit" UnitLiteral "{" ChannelCommandList "}"
    fun parseChannel(): ChannelNode? {
        if (!eat(TokenType.CHANNEL)) return null
        val channelId = currentToken.lexem.toIntOrNull() ?: run {
            println("ERR [${currentToken.row}:${currentToken.column}]: expected INT for channelId")
            return null
        }
        if (!eat(TokenType.INT)) return null
        val name = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.KIND)) return null
        val kind = currentToken.lexem
        if (!eatChannelKind()) return null
        if (!eat(TokenType.UNIT)) return null
        val unit = currentToken.lexem
        if (!eatUnitLiteral()) return null
        if (!eat(TokenType.LBRACE)) return null
        val commands = parseChannelCommandList()
        if (!eat(TokenType.RBRACE)) return null
        return ChannelNode(
            channelId = channelId, name = name,
            kind = kind, unit = unit, commands = commands
        )
    }

    fun eatChannelKind(): Boolean {
        val kinds = setOf(
            TokenType.WATER_LEVEL, TokenType.WATER_DEPTH, TokenType.PRESSURE,
            TokenType.TEMPERATURE, TokenType.HUMIDITY, TokenType.RAINFALL,
            TokenType.BATTERY, TokenType.SIGNAL
        )
        return if (currentToken.type in kinds) {
            currentToken = lexer.nextToken()
            true
        } else {
            println("ERR [${currentToken.row}:${currentToken.column}]: expected channel kind got(${currentToken.type})")
            false
        }
    }

    fun eatUnitLiteral(): Boolean {
        val units = setOf(
            TokenType.UNIT_M, TokenType.UNIT_CM, TokenType.UNIT_MM,
            TokenType.UNIT_HPA, TokenType.UNIT_C, TokenType.UNIT_PCT,
            TokenType.UNIT_V, TokenType.UNIT_DBM
        )
        return if (currentToken.type in units) {
            currentToken = lexer.nextToken()
            true
        } else {
            println("ERR [${currentToken.row}:${currentToken.column}]: expected unit got(${currentToken.type})")
            false
        }
    }

    // ChannelCommandList ::= ChannelCommand ChannelCommandList | ε
    fun parseChannelCommandList(): List<ChannelCommand> {
        val commands = mutableListOf<ChannelCommand>()
        val startTypes = setOf(
            TokenType.MEASUREMENT, TokenType.THRESHOLD,
            TokenType.DISPLAY, TokenType.LABEL
        )
        while (currentToken.type in startTypes) {
            val cmd = parseChannelCommand() ?: break
            commands.add(cmd)
        }
        return commands
    }

    // ChannelCommand ::= Measurement | Threshold | Display | Label
    fun parseChannelCommand(): ChannelCommand? {
        return when (currentToken.type) {
            TokenType.MEASUREMENT -> parseMeasurement()
            TokenType.THRESHOLD -> parseThreshold()
            TokenType.DISPLAY -> parseChannelDisplay()
            TokenType.LABEL -> parseChannelLabel()
            else -> {
                println("ERR [${currentToken.row}:${currentToken.column}]: unexpected channel command(${currentToken.type})")
                null
            }
        }
    }

    // Measurement ::= "measurement" DATETIME "value" NUMBER "status" StatusValue ";"
    fun parseMeasurement(): MeasurementNode? {
        if (!eat(TokenType.MEASUREMENT)) return null
        val datetime = currentToken.lexem
        if (!eat(TokenType.DATETIME)) return null
        if (!eat(TokenType.VALUE)) return null
        val value = currentToken.lexem.toDoubleOrNull() ?: run {
            println("ERR [${currentToken.row}:${currentToken.column}]: expected NUMBER for measurement value")
            return null
        }
        if (!eat(TokenType.NUMBER)) return null
        if (!eat(TokenType.STATUS)) return null
        val status = currentToken.lexem
        if (!eatStatusValue()) return null
        if (!eat(TokenType.SEMI)) return null
        return MeasurementNode(datetime = datetime, value = value, status = status)
    }

    // Threshold ::= "threshold" ThresholdDirection NUMBER "status" StatusValue ";"
    fun parseThreshold(): ThresholdNode? {
        if (!eat(TokenType.THRESHOLD)) return null
        val direction = currentToken.lexem
        if (!eatThresholdDirection()) return null
        val value = currentToken.lexem.toDoubleOrNull() ?: run {
            println("ERR [${currentToken.row}:${currentToken.column}]: expected NUMBER for threshold value")
            return null
        }
        if (!eat(TokenType.NUMBER)) return null
        if (!eat(TokenType.STATUS)) return null
        val status = currentToken.lexem
        if (!eatStatusValue()) return null
        if (!eat(TokenType.SEMI)) return null
        return ThresholdNode(direction = direction, value = value, status = status)
    }

    fun eatThresholdDirection(): Boolean {
        return when (currentToken.type) {
            TokenType.ABOVE, TokenType.BELOW -> {
                currentToken = lexer.nextToken()
                true
            }

            else -> {
                println("ERR [${currentToken.row}:${currentToken.column}]: expected above/below got(${currentToken.type})")
                false
            }
        }
    }

    fun eatStatusValue(): Boolean {
        return when (currentToken.type) {
            TokenType.STATUS_OK, TokenType.STATUS_WARNING, TokenType.STATUS_ERROR -> {
                currentToken = lexer.nextToken()
                true
            }

            else -> {
                println("ERR [${currentToken.row}:${currentToken.column}]: expected status (ok/warning/error) got(${currentToken.type})")
                false
            }
        }
    }

    fun parseChannelDisplay(): ChannelDisplayCommand? {
        if (!eat(TokenType.DISPLAY)) return null
        if (!eat(TokenType.COLOR_KW)) return null
        val color = currentToken.lexem
        if (!eat(TokenType.COLOR)) return null
        if (!eat(TokenType.SEMI)) return null
        return ChannelDisplayCommand(color = color)
    }

    fun parseChannelLabel(): ChannelLabelCommand? {
        if (!eat(TokenType.LABEL)) return null
        val label = currentToken.lexem
        if (!eat(TokenType.STRING)) return null
        if (!eat(TokenType.SEMI)) return null
        return ChannelLabelCommand(label = label)
    }
}