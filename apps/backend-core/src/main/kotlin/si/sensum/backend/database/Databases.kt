package si.sensum.backend.database

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import si.sensum.backend.channels.ChannelTable
import si.sensum.backend.customers.CustomerTable
import si.sensum.backend.measurements.MeasurementTable
import si.sensum.backend.stations.StationTable
import si.sensum.backend.stations.UserStationTable
import si.sensum.backend.users.UserTable

fun Application.configureDatabases() {
    val config = environment.config

    val jdbcUrl = config.property("database.jdbcUrl").getString()
    val user = config.property("database.user").getString()
    val password = config.property("database.password").getString()

    Database.connect(
        url = jdbcUrl,
        user = user,
        password = password
    )

    transaction {
        SchemaUtils.create(
            CustomerTable,
            UserTable,
            StationTable,
            UserStationTable,
            ChannelTable,
            MeasurementTable
        )
    }

    log.info("[DB] PostgreSQL database connected with Exposed ORM")
}