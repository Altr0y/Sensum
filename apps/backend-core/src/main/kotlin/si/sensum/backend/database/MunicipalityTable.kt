package si.sensum.backend.database

import org.jetbrains.exposed.v1.core.Table

object MunicipalityTable : Table("municipalities") {
    val id = integer("id").autoIncrement()
    val countryId = integer("country_id").references(CountryTable.id)
    val regionId = integer("region_id").references(RegionTable.id)
    val name = varchar("name", 50)
    val geometry = text("geometry").nullable()

    val dataSource = varchar("source", 20).default("UNKNOWN")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(countryId, name)
    }
}