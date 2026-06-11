package si.sensum.backend.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

object DatabaseSchemaMigration {

    fun createOrUpdateTables(
        vararg tables: Table
    ) {
        SchemaUtils.create(*tables)

        val statements = MigrationUtils.statementsRequiredForDatabaseMigration(
            tables = tables,
            withLogs = true
        )

        val alterColumnStatements = statements.filterNot { statement ->
            statement.contains("ADD CONSTRAINT", ignoreCase = true)
        }

        val constraintStatements = statements.filter { statement ->
            statement.contains("ADD CONSTRAINT", ignoreCase = true)
        }

        alterColumnStatements.forEach { statement ->
            exec(statement)
        }

        repairOrphanRowsBeforeConstraints()

        constraintStatements.forEach { statement ->
            exec(statement)
        }
    }

    private fun repairOrphanRowsBeforeConstraints() {
        deleteMeasurementsWithoutChannel()
        deleteChannelsWithoutStation()
        deleteUserStationsWithoutUser()
        deleteUserStationsWithoutStation()
    }

    private fun deleteMeasurementsWithoutChannel() {
        exec(
            """
            DELETE FROM measurements m
            WHERE NOT EXISTS (
                SELECT 1
                FROM channels c
                WHERE c.id = m.channel_id
            )
            """.trimIndent()
        )
    }

    private fun deleteChannelsWithoutStation() {
        exec(
            """
            DELETE FROM channels c
            WHERE NOT EXISTS (
                SELECT 1
                FROM stations s
                WHERE s.id = c.station_id
            )
            """.trimIndent()
        )
    }

    private fun deleteUserStationsWithoutUser() {
        exec(
            """
            DELETE FROM user_stations us
            WHERE NOT EXISTS (
                SELECT 1
                FROM users u
                WHERE u.id = us.user_id
            )
            """.trimIndent()
        )
    }

    private fun deleteUserStationsWithoutStation() {
        exec(
            """
            DELETE FROM user_stations us
            WHERE NOT EXISTS (
                SELECT 1
                FROM stations s
                WHERE s.id = us.station_id
            )
            """.trimIndent()
        )
    }

    private fun exec(statement: String) {
        TransactionManager.current().exec(statement)
    }
}