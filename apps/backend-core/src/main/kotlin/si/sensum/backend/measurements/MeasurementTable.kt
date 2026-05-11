package si.sensum.backend.measurements

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object MeasurementTable : Table("measurements") {
    val id = long("id").autoIncrement()
    val stationId = long("station_id")
    val channelId = integer("channel_id")
    val dateTime = datetime("date_time")
    val value = double("value")
    val status = integer("status")

    override val primaryKey = PrimaryKey(id)
}