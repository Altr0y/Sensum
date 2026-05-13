package si.sensum.backend.stations

import org.jetbrains.exposed.v1.core.Table

object UserStationTable : Table("user_stations") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val stationId = long("station_id")
    val permission = enumerationByName<Permission>(
        name = "permission",
        length = 20
    )

    override val primaryKey = PrimaryKey(id)
}