package si.sensum.backend.measurements

import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.core.Table

object MeasurementTable : Table("measurements") {
    val id = long("id").autoIncrement()
    val channelId = integer("channel_id")
    val dateTime = datetime("date_time")
    val value = float("value")
    val status = bool("status")

    override val primaryKey = PrimaryKey(id)
}
