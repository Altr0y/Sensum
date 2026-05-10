package si.sensum.backend.stations

import org.jetbrains.exposed.v1.core.Table

object StationTable : Table("Stations"){
    val id = long("id").autoIncrement()
    val customerId = integer("customerId")
    val locationDescription = varchar("locationDescription", 100)
    val alias = varchar("alias", 50)
    val serialNumber = varchar("serialNumber", 100)
    val longitude = double("longitude")
    val latitude = double("latitude")
    val location = varchar("location", 500)

    override val primaryKey = PrimaryKey(id)
}