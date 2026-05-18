package si.sensum.simulator.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelConfig(
    @SerialName("channel_id") val channelId: Int,
    val name: String,
    val unit: String,
    @SerialName("generator_type") val generatorType: GeneratorType,
    val resolution: Double = 0.0001,
    val mean: Double? = null,
    val std: Double? = null,
    @SerialName("min_absolute") val minAbsolute: Double? = null,
    @SerialName("max_absolute") val maxAbsolute: Double? = null,
    @SerialName("constant_value") val constantValue: Double? = null,
    @SerialName("hourly_profile") val hourlyProfile: List<Double> = emptyList(),
    @SerialName("weekly_profile") val weeklyProfile: Map<String, Double> = emptyMap(),
    @SerialName("seasonal_monthly_offsets") val seasonalMonthlyOffsets: Map<String, Double> = emptyMap(),
    @SerialName("outlier_rate") val outlierRate: Double = 0.0,
    @SerialName("autocorr_lag1") val autocorrLag1: Double? = null,
    @SerialName("correlation_group") val correlationGroup: String? = null,
    @SerialName("correlation_role") val correlationRole: String? = null,
    @SerialName("correlation_noise_std") val correlationNoiseStd: Double? = null,
    val note: String? = null
)