package si.sensum.backend.stations

data class UserStation(
    val id: Int,
    val userId: Int,
    val stationId: Long,
    val permission: Permission
)
