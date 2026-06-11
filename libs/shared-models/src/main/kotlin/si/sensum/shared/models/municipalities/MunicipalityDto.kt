package si.sensum.shared.models.municipalities

import kotlinx.serialization.Serializable
import si.sensum.shared.models.common.DataSourceDto

@Serializable
data class MunicipalityDto(
    val id: Int,
    val countryId: Int,
    val regionId: Int,
    val name: String,
    val geometry: String? = null,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)
