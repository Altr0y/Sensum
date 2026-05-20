package si.sensum.demo.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import si.sensum.demo.model.Measurement
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Timestamp

/// TO DO: fix measurement old model

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

    private fun connect(): Connection {
        Class.forName("org.postgresql.Driver")
        return DriverManager.getConnection(url, props)
    }

    override suspend fun getAll(): Result<List<Measurement>> = withContext(Dispatchers.IO) {
        runCatching {
            connect().use { conn ->
                val rs = conn.createStatement().executeQuery(
                    """
                        SELECT 
                            m.id,
                            s.id AS station_id,
                            s.alias AS station_name,
                            c.id AS channel_id,
                            c.name AS channel_name,
                            m.date_time,
                            m.value,
                            m.status
                        FROM measurements m
                        JOIN channels c ON m.channel_id = c.id
                        JOIN stations s ON c.station_id = s.id
                        ORDER BY c.id, m.date_time
                        """
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
                conn.autoCommit = false

                try{
                    getOrCreateCustomer(conn)
                    getOrCreateStation(conn, measurement)
                    getOrCreateChannel(conn, measurement)

                    val stmt = conn.prepareStatement(
                        """
                        INSERT INTO measurements 
                       (channel_id, date_time, value, status)
                       VALUES (?, ?, ?, ?)
                       RETURNING id
                       """
                    )

                    stmt.setInt(1, measurement.channelId)
                    stmt.setTimestamp(2, Timestamp.valueOf(measurement.dateTime))
                    stmt.setDouble(3, measurement.value)
                    stmt.setBoolean(4, measurement.status != 0)

                    val rs = stmt.executeQuery()
                    rs.next()

                    conn.commit()
                    measurement.copy(id = rs.getInt("id"))
                } catch(e: Exception){
                    conn.rollback()
                    throw e
                }
            }
        }
    }

    override suspend fun insertAll(measurements: List<Measurement>): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            connect().use { conn ->
                conn.autoCommit = false

                try{
                    getOrCreateCustomer(conn)

                    measurements.forEach { measurements ->
                        getOrCreateStation(conn, measurements)
                        getOrCreateChannel(conn, measurements)
                    }

                    val stmt = conn.prepareStatement(
                        """
                        INSERT INTO measurements
                       (channel_id, date_time, value, status)
                       VALUES (?, ?, ?, ?)
                       """
                    )

                    measurements.forEach { measurement ->
                        stmt.setInt(1, measurement.channelId)
                        stmt.setTimestamp(2, Timestamp.valueOf(measurement.dateTime))
                        stmt.setDouble(3, measurement.value)
                        stmt.setBoolean(4, measurement.status != 0)
                        stmt.addBatch()
                    }

                    val inserted = stmt.executeBatch().size
                    conn.commit()
                    inserted
                } catch (e: Exception){
                    conn.rollback()
                    throw e
                }
            }
        }
    }

    override suspend fun update(measurement: Measurement): Result<Measurement> = withContext(Dispatchers.IO) {
        runCatching {
            requireNotNull(measurement.id) { "Cannot update measurement without id" }

            connect().use { conn ->
                conn.autoCommit = false

                try{
                    getOrCreateCustomer(conn)
                    getOrCreateStation(conn, measurement)
                    getOrCreateChannel(conn, measurement)

                    val stmt = conn.prepareStatement(
                        """
                            UPDATE measurements
                            SET channel_id = ?, date_time = ?, value = ?, status = ?
                            WHERE id = ?
                        """
                    )

                    stmt.setInt(1, measurement.channelId)
                    stmt.setTimestamp(2, Timestamp.valueOf(measurement.dateTime))
                    stmt.setDouble(3, measurement.value)
                    stmt.setBoolean(4, measurement.status != 0)
                    stmt.setInt(5, measurement.id)

                    stmt.executeUpdate()
                    conn.commit()

                    measurement
                } catch (e: Exception){
                    conn.rollback()
                    throw e
                }
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

    private fun getOrCreateCustomer(conn: Connection) {
        val exists = conn.prepareStatement(
            "SELECT id FROM customers WHERE id = 1"
        )

        val rs = exists.executeQuery()
        if(rs.next()) return

        val insert = conn.prepareStatement(
            """
                INSERT INTO customers (id, name)
                VALUES (1, ?)
            """
        )

        insert.setString(1, "Imported desktop data")
        insert.executeUpdate()
    }

    private fun getOrCreateStation(conn: Connection, m: Measurement) {
        val exists = conn.prepareStatement(
            "SELECT id FROM stations WHERE id = ?"
        )

        exists.setLong(1, m.stationId.toLong())

        val rs = exists.executeQuery()
        if(rs.next()) return

        val insert = conn.prepareStatement(
            """
                INSERT INTO stations
                (id, customer_id, location_description, alias, serial_number, longitude, latitude, location)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """
        )

        insert.setLong(1, m.stationId.toLong())
        insert.setInt(2, 1) //default customer temp
        insert.setString(3, m.stationName)
        insert.setString(4, m.stationName)
        insert.setString(5, "")
        insert.setDouble(6, 0.0)
        insert.setDouble(7, 0.0)
        insert.setString(8, "")

        insert.executeUpdate()
    }

    private fun getOrCreateChannel(conn: Connection, m: Measurement) {
        val exists = conn.prepareStatement(
            "SELECT id FROM channels WHERE id = ?"
        )

        exists.setInt(1, m.channelId)

        val rs = exists.executeQuery()
        if(rs.next()) return

        val insert = conn.prepareStatement(
            """
                INSERT INTO channels
                (id, station_id, name, description, unit, enabled)
                VALUES (?, ?, ?, ?, ?, ?)
            """
        )

        insert.setInt(1, m.channelId)
        insert.setLong(2, m.stationId.toLong())
        insert.setString(3, m.channelName)
        insert.setString(4, m.channelName)
        insert.setString(5, "UNKNOWN")
        insert.setBoolean(6, true)

        insert.executeUpdate()
    }

    private fun ResultSet.toMeasurement() = Measurement(
        id = getInt("id"),
        stationId = getInt("station_id"),
        stationName = getString("station_name"),
        channelId = getInt("channel_id"),
        channelName = getString("channel_name"),
        dateTime = getTimestamp("date_time").toLocalDateTime(),
        value = getDouble("value"),
        status = if (getBoolean("status")) 1 else 0
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