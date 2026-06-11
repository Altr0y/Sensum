package si.sensum.shared.models.data

import kotlinx.serialization.Serializable
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.shared.models.common.DataSourceDto
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.stations.StationDto

@Serializable
data class DataImportCommand(
    val userId: Int = 0,
    val customerId: Int = 0,
    val source: DataSourceDto,
    val stations: List<StationDto> = emptyList(),
    val channels: List<ChannelDto> = emptyList(),
    val measurements: List<MeasurementDto> = emptyList()
)

@Serializable
data class DslImportCommand(
    val userId: Int = 0,
    val customerId: Int = 0,
    val source: String
)

@Serializable
data class SwsImportCommand(
    val userId: Int = 0,
    val customerId: Int = 0,
    val stationId: Long? = null,
    val channelId: Int? = null,
    val datetimeFrom: String? = null,
    val datetimeTo: String? = null
)

@Serializable
data class DataImportResult(
    val source: DataSourceDto,
    val stationCount: Int = 0,
    val channelCount: Int = 0,
    val measurementCount: Int = 0,
    val userStationCount: Int = 0,
    val stations: List<StationDto> = emptyList(),
    val channels: List<ChannelDto> = emptyList(),
    val measurements: List<MeasurementDto> = emptyList(),
    val geoJson: String? = null,
    val message: String = ""
)