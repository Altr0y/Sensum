package si.sensum.simulator

import kotlinx.serialization.json.Json
import si.sensum.simulator.config.StationConfig
import si.sensum.simulator.engine.GeneratedMeasurement
import si.sensum.simulator.engine.SimulatorEngine
import java.time.LocalDateTime

class SimulatorService {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val engine: SimulatorEngine by lazy {
        val configText = loadConfig()
        val stationConfig = json.decodeFromString<StationConfig>(configText)
        SimulatorEngine(stationConfig)
    }

    fun generateMeasurements(from: LocalDateTime, to: LocalDateTime): List<GeneratedMeasurement> {
        return engine.generate(from, to)
    }

    fun isRealDataAvailable(from: LocalDateTime, to: LocalDateTime): Boolean {
        val realFrom = LocalDateTime.parse(engine.stationConfig.realDataRange.from)
        val realTo = LocalDateTime.parse(engine.stationConfig.realDataRange.to)
        return !from.isBefore(realFrom) && !to.isAfter(realTo)
    }

    private fun loadConfig(): String {
        return SimulatorService::class.java
            .getResourceAsStream("/sensum_radartest_generator_config.json")
            ?.bufferedReader()
            ?.readText()
            ?: error("Config file not found in resources")
    }
}