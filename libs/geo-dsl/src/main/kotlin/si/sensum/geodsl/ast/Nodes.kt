package si.sensum.geodsl.ast

data class Coordinate(
    val longitude: Double,
    val latitude: Double
)

sealed class GeoCommand
data class PointCommand(val coordinate: Coordinate) : GeoCommand()
data class DisplayCommand(val color: String) : GeoCommand()
data class LabelCommand(val label: String) : GeoCommand()
data class SourceCommand(val source: String) : GeoCommand()

sealed class MunicipalityElement
data class RiverNode(val name: String, val commands: List<GeoCommand>) : MunicipalityElement()
data class LakeNode(val name: String, val commands: List<GeoCommand>) : MunicipalityElement()
data class AreaNode(val name: String, val commands: List<GeoCommand>) : MunicipalityElement()
data class FloodZoneNode(val name: String, val risk: String, val commands: List<GeoCommand>) : MunicipalityElement()
data class StationNode(
    val stationId: Int,
    val name: String,
    val coordinate: Coordinate,
    val commands: List<StationCommand>,
    val channels: List<ChannelNode>
) : MunicipalityElement()

sealed class StationCommand
data class StationSourceCommand(val source: String) : StationCommand()
data class StationDisplayCommand(val color: String) : StationCommand()
data class StationLabelCommand(val label: String) : StationCommand()

data class ChannelNode(
    val channelId: Int,
    val name: String,
    val kind: String,
    val unit: String,
    val commands: List<ChannelCommand>
)

sealed class ChannelCommand
data class MeasurementNode(
    val datetime: String,
    val value: Double,
    val status: String
) : ChannelCommand()

data class ThresholdNode(
    val direction: String,
    val value: Double,
    val status: String
) : ChannelCommand()

data class ChannelDisplayCommand(val color: String) : ChannelCommand()
data class ChannelLabelCommand(val label: String) : ChannelCommand()

data class MunicipalityNode(
    val name: String,
    val elements: List<MunicipalityElement>
)

data class RegionNode(
    val name: String,
    val municipalities: List<MunicipalityNode>
)

data class CountryNode(
    val name: String,
    val regions: List<RegionNode>
)