package si.sensum.simulator.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GeneratorType {
    @SerialName("random_walk") RANDOM_WALK,
    @SerialName("constant") CONSTANT
}