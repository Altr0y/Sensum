package si.sensum.backend.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime
import si.sensum.shared.models.common.DataSourceDto

object MeasurementTable : Table("measurements") {
    val id = long("id").autoIncrement()
    val channelId = integer("channel_id")
    val dateTime = datetime("date_time")
    val value = float("value")
    val status = bool("status")

    val dataSource = varchar("source", 20).default("UNKNOWN")

    override val primaryKey = PrimaryKey(id)
}