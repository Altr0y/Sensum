package si.sensum.simulator

import si.sensum.geodsl.ast.StationNode

data class StationWithContext(
    val station: StationNode,
    val floodRisk: String?,
    val nearRiver: Boolean
)