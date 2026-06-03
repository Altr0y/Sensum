package si.sensum.backend.database

import org.jetbrains.exposed.v1.core.Table

object StationTable : Table("stations"){
    val id = long("id").autoIncrement()
    val customerId = integer("customer_id")
    val locationDescription = varchar("location_description", 100)
    val alias = varchar("alias", 50)
    val serialNumber = varchar("serial_number", 100)
    val longitude = double("longitude")
    val latitude = double("latitude")
    val location = varchar("location", 500)

    override val primaryKey = PrimaryKey(id)
}