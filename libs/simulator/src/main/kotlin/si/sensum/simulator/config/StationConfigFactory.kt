package si.sensum.simulator.config

import si.sensum.geodsl.ast.*
import si.sensum.simulator.engine.RandomWalkGenerator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object StationConfigFactory {

    fun create(
        stationId: Int,
        stationName: String,
        location: PointNode,
        channelKinds: List<ChannelKind>,
        floodRisk: String?,
        nearRiver: Boolean,
        from: LocalDateTime,
        to: LocalDateTime,
        intervalMinutes: Long = 60L
    ): StationNode {
        val channels = channelKinds.mapIndexed { index, kind ->
            val config = buildChannelConfig(900 + index, kind, floodRisk, nearRiver)
            val generator = RandomWalkGenerator(config)
            val measurements = generateMeasurements(generator, from, to, intervalMinutes, floodRisk)
            ChannelNode(
                id = 900 + index,
                name = config.name,
                kind = kind,
                unit = config.unit,
                measurements = measurements
            )
        }
        return StationNode(
            id = stationId,
            name = stationName,
            location = location,
            channels = channels
        )
    }

    private fun generateMeasurements(
        generator: RandomWalkGenerator,
        from: LocalDateTime,
        to: LocalDateTime,
        intervalMinutes: Long,
        floodRisk: String?
    ): List<MeasurementNode> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val measurements = mutableListOf<MeasurementNode>()
        var current = from
        generator.reset()
        while (!current.isAfter(to)) {
            val value = generator.generate(
                hour = current.hour,
                dayOfWeek = current.dayOfWeek.name.lowercase()
                    .replaceFirstChar { it.uppercase() },
                month = current.monthValue.toString()
            )
            measurements.add(
                MeasurementNode(
                    datetime = current.format(formatter),
                    value = value,
                    status = determineStatus(value, floodRisk)
                )
            )
            current = current.plusMinutes(intervalMinutes)
        }
        return measurements
    }

    private fun determineStatus(value: Double, floodRisk: String?): MeasurementStatus {
        return when (floodRisk) {
            "often"     -> if (value > 4.0) MeasurementStatus.CRITICAL
            else if (value > 2.5) MeasurementStatus.WARNING
            else MeasurementStatus.OK
            "rare"      -> if (value > 3.0) MeasurementStatus.WARNING
            else MeasurementStatus.OK
            else        -> MeasurementStatus.OK
        }
    }

    fun buildChannelConfig(
        channelId: Int,
        kind: ChannelKind,
        floodRisk: String?,
        nearRiver: Boolean
    ): ChannelConfig = when (kind) {
        ChannelKind.WATER_LEVEL -> waterLevelConfig(channelId, floodRisk, nearRiver)
        ChannelKind.TEMPERATURE -> temperatureConfig(channelId)
        ChannelKind.RAINFALL    -> rainfallConfig(channelId, floodRisk)
        ChannelKind.FLOW_RATE   -> flowRateConfig(channelId, floodRisk, nearRiver)
    }

    private fun waterLevelConfig(
        channelId: Int,
        floodRisk: String?,
        nearRiver: Boolean
    ): ChannelConfig {
        val (mean, std, min, max) = when (floodRisk) {
            "often"     -> if (nearRiver) listOf(3.5, 0.8, 0.5, 6.0)
            else           listOf(2.5, 0.6, 0.3, 5.0)
            "rare"      -> if (nearRiver) listOf(2.0, 0.5, 0.2, 4.0)
            else           listOf(1.5, 0.4, 0.1, 3.0)
            "very_rare" -> listOf(1.0, 0.3, 0.1, 2.5)
            else        -> listOf(0.8, 0.2, 0.1, 1.5)
        }
        return ChannelConfig(
            channelId = channelId,
            name = "Water level",
            unit = "m",
            generatorType = GeneratorType.RANDOM_WALK,
            mean = mean, std = std,
            minAbsolute = min, maxAbsolute = max,
            autocorrLag1 = 0.92
        )
    }

    private fun temperatureConfig(channelId: Int) = ChannelConfig(
        channelId = channelId,
        name = "Temperature",
        unit = "°C",
        generatorType = GeneratorType.RANDOM_WALK,
        mean = 10.5, std = 2.0,
        minAbsolute = -5.0, maxAbsolute = 30.0,
        autocorrLag1 = 0.97
    )

    private fun rainfallConfig(channelId: Int, floodRisk: String?) = ChannelConfig(
        channelId = channelId,
        name = "Rainfall",
        unit = "mm",
        generatorType = GeneratorType.RANDOM_WALK,
        mean = if (floodRisk != null) 3.5 else 1.5,
        std = 2.0,
        minAbsolute = 0.0,
        maxAbsolute = if (floodRisk == "often") 80.0 else 40.0,
        autocorrLag1 = 0.4
    )

    private fun flowRateConfig(
        channelId: Int,
        floodRisk: String?,
        nearRiver: Boolean
    ) = ChannelConfig(
        channelId = channelId,
        name = "Flow rate",
        unit = "m³/s",
        generatorType = GeneratorType.RANDOM_WALK,
        mean = when (floodRisk) {
            "often"     -> if (nearRiver) 85.0 else 50.0
            "rare"      -> 30.0
            "very_rare" -> 15.0
            else        -> 8.0
        },
        std = 10.0,
        minAbsolute = 0.5,
        maxAbsolute = if (floodRisk == "often" && nearRiver) 500.0 else 200.0,
        autocorrLag1 = 0.88
    )
}