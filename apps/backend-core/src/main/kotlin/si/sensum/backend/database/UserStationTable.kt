package si.sensum.backend.database

import org.jetbrains.exposed.v1.core.Table
import si.sensum.backend.domain.station.Permission

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