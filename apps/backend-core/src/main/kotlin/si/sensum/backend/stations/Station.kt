package si.sensum.backend.stations

data class Station(
    val id: Long,
    val customerId: Int,
    val locationDescription: String,
    val alias: String,
    val serialNumber: String,
    val longitude: String,
    val latitude: Double,
    val location: Double
)
