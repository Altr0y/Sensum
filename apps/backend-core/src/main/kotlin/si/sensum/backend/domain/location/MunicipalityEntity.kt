package si.sensum.backend.domain.location

import si.sensum.shared.models.common.DataSourceDto

data class MunicipalityEntity(
    val id: Int,
    val countryId: Int,
    val regionId: Int,
    val name: String,
    val geometry: String?,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)
