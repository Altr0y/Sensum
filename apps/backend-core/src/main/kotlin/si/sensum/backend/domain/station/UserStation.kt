package si.sensum.backend.domain.station

data class UserStation(
    val id: Int,
    val userId: Int,
    val stationId: Long,
    val permission: Permission
)