package si.sensum.simulator

import si.sensum.geodsl.ast.*
import si.sensum.geodsl.import.convertFloodZonesFromString
import si.sensum.geodsl.import.convertMunicipalitiesFromString
import si.sensum.geodsl.import.convertRegionsFromString
import si.sensum.geodsl.import.convertRiversFromString
import si.sensum.geodsl.lexer.Lexer
import si.sensum.geodsl.parser.Parser
import si.sensum.geodsl.spatial.SpatialUtils
import si.sensum.simulator.config.StationConfigFactory

class SpatialSimulatorService(
    private val floodZonesDir: String,
    private val riversPath: String,
    private val regionsPath: String,
    private val municipalitiesPath: String
) {
    private val floodZones: List<FloodZoneNode> by lazy { loadFloodZones() }
    private val rivers: List<RiverNode> by lazy { loadRivers() }
    private val regions: List<RegionNode> by lazy { loadRegions() }
    private val municipalities: List<MunicipalityNode> by lazy { loadMunicipalities() }

    fun handle(request: SimulateRequest): List<StationWithContext> = when (request) {
        is SimulateRequest.Full -> handleFull(request)
        is SimulateRequest.StationsOnly -> handleStationsOnly(request)
        is SimulateRequest.ChannelsOnly -> handleChannelsOnly(request)
        is SimulateRequest.MeasurementsOnly -> handleMeasurementsOnly(request)
    }

    private fun handleFull(request: SimulateRequest.Full): List<StationWithContext> {
        val polygon = resolvePolygon(request.regionName, request.municipalityName)
        return (1..request.count).map { i ->
            val location = if (polygon != null)
                SpatialUtils.randomPointInPolygon(polygon)
            else
                randomPointInSlovenia()
            val floodRisk = SpatialUtils.detectFloodRisk(location, floodZones)
            val nearRiver = rivers.any {
                SpatialUtils.isPointNearPolyline(location, it.points, 500)
            }
            val station = StationConfigFactory.create(
                stationId = generateId(),
                stationName = "${request.prefix} $i",
                location = location,
                channelKinds = request.channelKinds,
                floodRisk = floodRisk,
                nearRiver = nearRiver,
                from = request.from,
                to = request.to,
                intervalMinutes = request.intervalMinutes
            )
            StationWithContext(station, floodRisk, nearRiver)
        }
    }


    private fun handleStationsOnly(request: SimulateRequest.StationsOnly): List<StationWithContext> {
        val polygon = resolvePolygon(request.regionName, request.municipalityName)
        return (1..request.count).map { i ->
            val location = if (polygon != null)
                SpatialUtils.randomPointInPolygon(polygon)
            else
                randomPointInSlovenia()
            val floodRisk = SpatialUtils.detectFloodRisk(location, floodZones)
            val nearRiver = rivers.any {
                SpatialUtils.isPointNearPolyline(location, it.points, 500)
            }
            val station = StationNode(
                id = generateId(),
                name = "${request.prefix} $i",
                location = location,
                channels = emptyList()
            )
            StationWithContext(station, floodRisk, nearRiver)
        }
    }

    private fun handleChannelsOnly(request: SimulateRequest.ChannelsOnly): List<StationWithContext> {
        val channels = request.channelKinds.mapIndexed { index, kind ->
            val config = StationConfigFactory.buildChannelConfig(
                channelId = 900 + index,
                kind = kind,
                floodRisk = null,
                nearRiver = false
            )
            ChannelNode(
                id = 900 + index,
                name = config.name,
                kind = kind,
                unit = config.unit,
                measurements = emptyList()
            )
        }
        return listOf(
            StationWithContext(
                station = StationNode(
                    id = request.stationId,
                    name = "",
                    location = PointNode(0.0, 0.0),
                    channels = channels
                ),
                floodRisk = null,
                nearRiver = false
            )
        )
    }

    private fun handleMeasurementsOnly(request: SimulateRequest.MeasurementsOnly): List<StationWithContext> {
        return listOf(
            StationWithContext(
                station = StationNode(
                    id = request.stationId,
                    name = "",
                    location = PointNode(0.0, 0.0),
                    channels = request.channelIds.map { channelId ->
                        ChannelNode(
                            id = channelId,
                            name = "",
                            kind = ChannelKind.WATER_LEVEL,
                            unit = "",
                            measurements = emptyList()
                        )
                    }
                ),
                floodRisk = null,
                nearRiver = false
            )
        )
    }

    private fun resolvePolygon(
        regionName: String?,
        municipalityName: String?
    ): List<PointNode>? = when {
        municipalityName != null ->
            municipalities.firstOrNull { it.name == municipalityName }?.polygon

        regionName != null ->
            regions.firstOrNull { it.name == regionName }?.points

        else -> null
    }

    private fun randomPointInSlovenia() = PointNode(
        lon = 13.3 + Math.random() * (16.6 - 13.3),
        lat = 45.4 + Math.random() * (46.9 - 45.4)
    )

    private fun generateId() = (10000..99999).random()

    // Parsiranje prek obstojecih convert funkcij + Lexer/Parser
    private fun parseSensumBody(body: String): List<GeoElement> {
        val lexer = Lexer(body)
        val tokens = lexer.tokenize()
        val program = Parser(tokens, lexer).parseProgram()
        return program.countries.firstOrNull()?.geoElements ?: emptyList()
    }

    private fun parseMunicipalityBody(body: String): List<MunicipalityNode> {
        val wrapped = """country "Slovenia" { $body }"""
        val lexer = Lexer(wrapped)
        val tokens = lexer.tokenize()
        val program = Parser(tokens, lexer).parseProgram()
        return program.countries.firstOrNull()?.municipalities ?: emptyList()
    }

    private fun readResource(path: String): String {
        return SpatialSimulatorService::class.java
            .getResourceAsStream("/$path")
            ?.bufferedReader()
            ?.readText()
            ?: error("Resource not found: $path")
    }


    private fun loadFloodZones(): List<FloodZoneNode> {
        return listOf("Often" to "often", "Rare" to "rare", "Very rare" to "very_rare")
            .flatMap { (fileName, risk) ->
                val body = convertFloodZonesFromString(
                    readResource("$floodZonesDir/Floods - $fileName.geojson"), risk
                )
                parseSensumBody(body).filterIsInstance<FloodZoneNode>()
            }
    }

    private fun loadRivers(): List<RiverNode> {
        val body = convertRiversFromString(readResource(riversPath))
        return parseSensumBody(body).filterIsInstance<RiverNode>()
    }

    private fun loadRegions(): List<RegionNode> {
        val body = convertRegionsFromString(readResource(regionsPath))
        return parseSensumBody(body).filterIsInstance<RegionNode>()
    }

    private fun loadMunicipalities(): List<MunicipalityNode> {
        val body = convertMunicipalitiesFromString(readResource(municipalitiesPath))
        return parseMunicipalityBody(body)
    }


}