package si.sensum.backend.stations

import org.jetbrains.exposed.v1.core.Table

object UserStationTable : Table("UserStations") {
    val id = integer("id").autoIncrement()
    val userId = integer("userId")
    val stationId = long("stationId")
    val permission = enumerationByName<Permission>(
        name = "permission",
        length = 20
    )

    override val primaryKey = PrimaryKey(id)
}