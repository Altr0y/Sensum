package si.sensum.backend.domain.location

import si.sensum.shared.models.common.DataSourceDto

data class CountryEntity(
    val id: Int,
    val code: Int,
    val name: String,
    val originName: String?,
    val geometry: String?,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)
