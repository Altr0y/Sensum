package si.sensum.shared.models.regions

import kotlinx.serialization.Serializable
import si.sensum.shared.models.common.DataSourceDto

@Serializable
data class RegionDto(
    val id: Int,
    val countryId: Int,
    val name: String,
    val geometry: String? = null,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)