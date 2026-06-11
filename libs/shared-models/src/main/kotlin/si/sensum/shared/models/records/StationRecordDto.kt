package si.sensum.shared.models.records

import kotlinx.serialization.Serializable
import si.sensum.shared.models.common.DataSourceDto

@Serializable
data class StationRecordDto(
    val stationId: Long,
    val name: String? = null,
    val serialNumber: String? = null,
    val description: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val countryId: Int? = null,
    val regionId: Int? = null,
    val municipalityId: Int? = null,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)