package si.sensum.shared.models.countries

import kotlinx.serialization.Serializable
import si.sensum.shared.models.common.DataSourceDto

@Serializable
data class CountryDto(
    val id: Int,
    val code: Int,
    val name: String,
    val originName: String?,
    val geometry: String? = null,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)