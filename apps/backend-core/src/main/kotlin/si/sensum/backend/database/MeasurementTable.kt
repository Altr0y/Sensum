package si.sensum.backend.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object MeasurementTable : Table("measurements") {
    val id = long("id").autoIncrement()
    val channelId = integer("channel_id")
    val dateTime = datetime("date_time")
    val value = float("value")
    val status = bool("status")

    override val primaryKey = PrimaryKey(id)
}