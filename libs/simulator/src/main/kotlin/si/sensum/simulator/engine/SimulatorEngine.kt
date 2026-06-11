package si.sensum.simulator.engine

import si.sensum.simulator.config.ChannelConfig
import si.sensum.simulator.config.GeneratorType
import si.sensum.simulator.config.StationConfig
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

data class GeneratedMeasurement(
    val channelId: Int,
    val dateTime: LocalDateTime,
    val value: Double,
    val status: Int = 0
)

class SimulatorEngine(val stationConfig: StationConfig) {

    private val generators = mutableMapOf<Int, Any>()

    init {
        stationConfig.channels.forEach { channel ->
            generators[channel.channelId] = createGenerator(channel)
        }
    }

    fun generate(from: LocalDateTime, to: LocalDateTime): List<GeneratedMeasurement> {
        val results = mutableListOf<GeneratedMeasurement>()
        val intervalMinutes = stationConfig.measurementIntervalMinutes.toLong()

        var current = from
        while (!current.isAfter(to)) {
            val hour = current.hour
            val dayOfWeek = current.dayOfWeek
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                .lowercase()
            val month = current.month
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                .lowercase()

            stationConfig.channels.forEach { channel ->
                val value = generateValue(channel, hour, dayOfWeek, month)
                results.add(GeneratedMeasurement(channel.channelId, current, value))
            }

            current = current.plusMinutes(intervalMinutes)
        }

        return results
    }

    private fun generateValue(channel: ChannelConfig, hour: Int, dayOfWeek: String, month: String): Double {
        return when (channel.generatorType) {
            GeneratorType.CONSTANT -> {
                (generators[channel.channelId] as ConstantGenerator).generate()
            }

            GeneratorType.RANDOM_WALK -> {
                (generators[channel.channelId] as RandomWalkGenerator).generate(hour, dayOfWeek, month)
            }
        }
    }

    private fun createGenerator(channel: ChannelConfig): Any {
        return when (channel.generatorType) {
            GeneratorType.CONSTANT -> ConstantGenerator(channel)
            GeneratorType.RANDOM_WALK -> RandomWalkGenerator(channel)
        }
    }

    fun reset() {
        stationConfig.channels.forEach { channel ->
            val generator = generators[channel.channelId]
            if (generator is RandomWalkGenerator) generator.reset()
        }
    }
}