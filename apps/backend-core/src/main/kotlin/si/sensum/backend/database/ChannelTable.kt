package si.sensum.backend.database

import org.jetbrains.exposed.v1.core.Table
import si.sensum.backend.domain.measurement.MeasurementUnit

object ChannelTable : Table("channels") {
    val id = integer("id").autoIncrement()
    val stationId = long("station_id").references(StationTable.id)
    val name = varchar("name", 50)
    val description = varchar("description", 500)
    val unit = enumerationByName<MeasurementUnit>(
        name = "unit",
        length = 20
    )
    val enabled = bool("enabled")

    val dataSource = varchar("source", 20).default("UNKNOWN")

    override val primaryKey = PrimaryKey(id)
}