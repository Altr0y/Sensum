package si.sensum.backend.measurements

import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.core.Table

object MeasurementTable : Table("Measurements") {
    val id = long("id").autoIncrement()
    val channelId = integer("channelId")
    val date = datetime("date")
    val value = float("value")
    val status = bool("status")

    override val primaryKey = PrimaryKey(id)
}
