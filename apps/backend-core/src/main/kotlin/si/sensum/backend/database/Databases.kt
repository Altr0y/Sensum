package si.sensum.backend.database

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import si.sensum.logging.Logger

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
        exec("SELECT 1")
    }

    Logger.log.info { "[DB] PostgreSQL database connected with Exposed ORM" }
}