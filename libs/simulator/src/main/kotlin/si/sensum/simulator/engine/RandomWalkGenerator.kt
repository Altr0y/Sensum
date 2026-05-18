package si.sensum.simulator.engine

import si.sensum.simulator.config.ChannelConfig
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class RandomWalkGenerator(private val config: ChannelConfig) {
    private var currentValue: Double? = null

    fun generate(hour: Int, dayOfWeek: String, month: String): Double {
        val hourlyBase = if (config.hourlyProfile.isNotEmpty()) config.hourlyProfile[hour]
        else config.mean ?: 0.0
        val weeklyOffset = weeklyOffset(dayOfWeek)
        val seasonalOffset = config.seasonalMonthlyOffsets[month] ?: 0.0
        val target = hourlyBase + weeklyOffset + seasonalOffset

        if (currentValue == null) currentValue = target

        val smoothing = config.autocorrLag1 ?: 0.95
        val noise = Random.nextGaussian() * (config.std ?: 0.0) * (1.0 - smoothing) * 50.0
        currentValue = currentValue!! * smoothing + target * (1.0 - smoothing) + noise
        val clipped = clip(currentValue!!, seasonalOffset)
        currentValue = clipped
        return round(clipped)
    }

    fun reset() {
        currentValue = null
    }

    private fun weeklyOffset(dayOfWeek: String): Double {
        if (config.weeklyProfile.isEmpty()) return 0.0
        val profileMean = config.weeklyProfile.values.average()
        return (config.weeklyProfile[dayOfWeek] ?: profileMean) - profileMean
    }

    private fun clip(value: Double, seasonalOffset: Double): Double {
        val min = (config.minAbsolute ?: Double.MIN_VALUE) + seasonalOffset
        val max = (config.maxAbsolute ?: Double.MAX_VALUE) + seasonalOffset
        return min(max, max(min, value))
    }

    private fun round(value: Double): Double {
        val factor = 1.0 / config.resolution
        return kotlin.math.round(value * factor) / factor
    }
}