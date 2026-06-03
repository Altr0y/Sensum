package si.sensum.backend.domain.station

import si.sensum.shared.models.common.DataSourceDto

data class StationEntity(
    val id: Long,
    val customerId: Int,
    val locationDescription: String,
    val alias: String,
    val serialNumber: String,
    val longitude: Double,
    val latitude: Double,
    val location: String,
    val source: DataSourceDto = DataSourceDto.UNKNOWN
)