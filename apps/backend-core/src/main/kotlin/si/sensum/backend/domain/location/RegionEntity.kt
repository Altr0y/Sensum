package si.sensum.backend.domain.location

import si.sensum.shared.models.common.DataSourceDto

data class RegionEntity(
    val id: Int,
    val countryId: Int,
    val name: String,
    val geometry: String?,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)
