package si.sensum.geodsl.ast

data class ProgramNode(val countries: List<CountryNode>)

data class CountryNode(
    val name: String,
    val layers: List<LayerNode>,
    val dataSources: List<DataSourceNode>,
    val municipalities: List<MunicipalityNode>,
    val stations: List<StationNode>,
    val autoResolves: List<AutoResolveNode>,
    val exports: List<ExportNode>,
    val geoElements: List<GeoElement> = emptyList()
)

data class LayerNode(
    val name: String,
    val filePath: String,
    val elements: List<GeoElement> = emptyList()
)

data class DataSourceNode(val name: String, val tableName: String)

data class MunicipalityNode(val name: String, val polygon: List<PointNode>)

data class PointNode(val lon: Double, val lat: Double)

data class StationNode(
    val id: Int,
    val name: String,
    val location: PointNode,
    val channels: List<ChannelNode>
)

enum class ChannelKind { WATER_LEVEL, TEMPERATURE, RAINFALL, FLOW_RATE }

data class ChannelNode(
    val id: Int,
    val name: String,
    val kind: ChannelKind,
    val unit: String,
    val measurements: List<MeasurementNode>
)

enum class MeasurementStatus { OK, WARNING, CRITICAL, ERROR }

data class MeasurementNode(
    val datetime: String,
    val value: Double,
    val status: MeasurementStatus
)

sealed class GeoElement
data class RiverNode(val name: String, val points: List<PointNode>) : GeoElement()
data class LakeNode(val name: String, val points: List<PointNode>) : GeoElement()
data class RegionNode(val name: String, val points: List<PointNode>) : GeoElement()
data class FloodZoneNode(
    val name: String,
    val risk: String,
    val points: List<PointNode>
) : GeoElement()

data class AutoResolveNode(val targetEntity: String, val rules: List<ResolveRuleNode>)

sealed class ResolveRuleNode {
    abstract val required: Boolean
}

data class BelongsToRuleNode(val typeName: String, override val required: Boolean) : ResolveRuleNode()
data class InsideRuleNode(val typeName: String, override val required: Boolean) : ResolveRuleNode()
data class NearRuleNode(val typeName: String, val withinMeters: Int, override val required: Boolean) : ResolveRuleNode()

enum class ExportFormat { GEOJSON, CSV, JSON }

data class ExportNode(
    val format: ExportFormat,
    val outputPath: String,
    val options: List<ExportOptionNode>
)

sealed class ExportOptionNode
data class StationGeometryOption(val geometryType: String) : ExportOptionNode()
object SpatialRefsOption : ExportOptionNode()
data class LatestMeasurementsOption(val after: String? = null) : ExportOptionNode()
data class RawIdentOption(val name: String) : ExportOptionNode()