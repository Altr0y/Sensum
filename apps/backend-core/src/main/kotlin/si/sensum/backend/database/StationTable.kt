package si.sensum.backend.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock
import si.sensum.shared.models.common.DataSourceDto

object StationTable : Table("stations") {
    val id = long("id").autoIncrement()
    val customerId = integer("customer_id").references(CustomerTable.id)
    val countryId = integer("country_id").references(CountryTable.id).nullable()
    val regionId = integer("region_id").references(RegionTable.id).nullable()
    val municipalityId = integer("municipality_id").references(MunicipalityTable.id).nullable()
    val locationDescription = varchar("location_description", 100)
    val alias = varchar("alias", 50)
    val serialNumber = varchar("serial_number", 100)
    val longitude = double("longitude")
    val latitude = double("latitude")
    val location = varchar("location", 500)
    val dataSource = varchar("source", 20).default("UNKNOWN")
    val createdAt = datetime("created_at").nullable()
        .clientDefault { Clock.System.now().toLocalDateTime(TimeZone.UTC) }

    override val primaryKey = PrimaryKey(id)
}