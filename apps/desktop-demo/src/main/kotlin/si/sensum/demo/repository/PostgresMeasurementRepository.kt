package si.sensum.demo.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import si.sensum.demo.model.Measurement
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Timestamp

class PostgresMeasurementRepository(
    host: String = "localhost",
    port: Int = 5432,
    database: String = "sensum",
    user: String = "sensum",
    password: String = "sensum"
) : DatabaseRepository {

    private val url = "jdbc:postgresql://$host:$port/$database"
    private val props = java.util.Properties().apply {
        setProperty("user", user)
        setProperty("password", password)
    }

    private fun connect(): java.sql.Connection {
        Class.forName("org.postgresql.Driver")
        return DriverManager.getConnection(url, props)
    }

    override suspend fun getAll(): Result<List<Measurement>> = withContext(Dispatchers.IO) {
        runCatching {
            connect().use { conn ->
                val rs = conn.createStatement().executeQuery(
                    "SELECT * FROM measurements ORDER BY channel_id, date_time"
                )
                val list = mutableListOf<Measurement>()
                while (rs.next()) list.add(rs.toMeasurement())
                list
            }
        }
    }

    override suspend fun insert(measurement: Measurement): Result<Measurement> = withContext(Dispatchers.IO) {
        runCatching {
            connect().use { conn ->
                val stmt = conn.prepareStatement(
                    """INSERT INTO measurements 
                       (station_id, station_name, channel_id, channel_name, date_time, value, status)
                       VALUES (?, ?, ?, ?, ?, ?, ?)
                       RETURNING *"""
                )
                stmt.setMeasurement(measurement)
                val rs = stmt.executeQuery()
                rs.next()
                rs.toMeasurement()
            }
        }
    }

    override suspend fun insertAll(measurements: List<Measurement>): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            connect().use { conn ->
                val stmt = conn.prepareStatement(
                    """INSERT INTO measurements
                       (station_id, station_name, channel_id, channel_name, date_time, value, status)
                       VALUES (?, ?, ?, ?, ?, ?, ?)"""
                )
                measurements.forEach { m ->
                    stmt.setMeasurement(m)
                    stmt.addBatch()
                }
                stmt.executeBatch().size
            }
        }
    }

    override suspend fun update(measurement: Measurement): Result<Measurement> = withContext(Dispatchers.IO) {
        runCatching {
            requireNotNull(measurement.id) { "Cannot update measurement without id" }
            connect().use { conn ->
                val stmt = conn.prepareStatement(
                    """UPDATE measurements SET
                       station_id = ?, station_name = ?, channel_id = ?, channel_name = ?,
                       date_time = ?, value = ?, status = ?
                       WHERE id = ?
                       RETURNING *"""
                )
                stmt.setMeasurement(measurement)
                stmt.setInt(8, measurement.id)
                val rs = stmt.executeQuery()
                rs.next()
                rs.toMeasurement()
            }
        }
    }

    override suspend fun delete(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            connect().use { conn ->
                conn.prepareStatement("DELETE FROM measurements WHERE id = ?").apply {
                    setInt(1, id)
                    executeUpdate()
                }
                Unit
            }
        }
    }

    override suspend fun deleteAll(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            connect().use { conn ->
                conn.createStatement().executeUpdate("DELETE FROM measurements")
                Unit
            }
        }
    }

    private fun ResultSet.toMeasurement() = Measurement(
        id = getInt("id"),
        stationId = getInt("station_id"),
        stationName = getString("station_name"),
        channelId = getInt("channel_id"),
        channelName = getString("channel_name"),
        dateTime = getTimestamp("date_time").toLocalDateTime(),
        value = getDouble("value"),
        status = getInt("status")
    )

    private fun java.sql.PreparedStatement.setMeasurement(m: Measurement) {
        setInt(1, m.stationId)
        setString(2, m.stationName)
        setInt(3, m.channelId)
        setString(4, m.channelName)
        setTimestamp(5, Timestamp.valueOf(m.dateTime))
        setDouble(6, m.value)
        setInt(7, m.status)
    }
}