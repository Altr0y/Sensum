package si.sensum.backend.domain.station

data class StationEntity(
    val id: Long,
    val customerId: Int,
    val locationDescription: String,
    val alias: String,
    val serialNumber: String,
    val longitude: Double,
    val latitude: Double,
    val location: String
)