package si.sensum.backend.channels

import org.jetbrains.exposed.v1.core.Table
import si.sensum.backend.measurements.MeasurementUnit

object ChannelTable : Table("Channels") {
    val id = integer("id").autoIncrement()
    val stationId = long("stationId")
    val name = varchar("name", 50)
    val description = varchar("description", 500)
    val unit = enumerationByName<MeasurementUnit>(
        name = "unit",
        length = 20
    )
    val enabled = bool("enabled")

    override val primaryKey = PrimaryKey(id)
}
