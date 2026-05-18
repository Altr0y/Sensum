package si.sensum.simulator.engine

import si.sensum.simulator.config.ChannelConfig

class ConstantGenerator(private val config: ChannelConfig) {

    fun generate(): Double {
        return config.constantValue ?: 0.0
    }
}