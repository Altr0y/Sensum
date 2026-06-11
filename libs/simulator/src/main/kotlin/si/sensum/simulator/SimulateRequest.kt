package si.sensum.simulator

import si.sensum.geodsl.ast.ChannelKind
import si.sensum.geodsl.ast.PointNode
import java.time.LocalDateTime

sealed class SimulateRequest {

    // 1. Kompletno - postaja + kanali + meritve
    data class Full(
        val count: Int,
        val prefix: String,
        val channelKinds: List<ChannelKind>,
        val regionName: String? = null,
        val municipalityName: String? = null,
        val polygon: List<PointNode>? = null,
        val from: LocalDateTime,
        val to: LocalDateTime,
        val intervalMinutes: Long = 60L
    ) : SimulateRequest()

    // 2. Samo postaje - brez kanalov in meritev
    data class StationsOnly(
        val count: Int,
        val prefix: String,
        val regionName: String? = null,
        val municipalityName: String? = null
    ) : SimulateRequest()

    // 3. Samo kanali - obstoječa postaja
    data class ChannelsOnly(
        val stationId: Int,
        val channelKinds: List<ChannelKind>
    ) : SimulateRequest()

    // 4. Samo meritve - obstoječa postaja + obstoječi kanali
    data class MeasurementsOnly(
        val stationId: Int,
        val channelIds: List<Int>,
        val from: LocalDateTime,
        val to: LocalDateTime,
        val intervalMinutes: Long = 60L
    ) : SimulateRequest()
}