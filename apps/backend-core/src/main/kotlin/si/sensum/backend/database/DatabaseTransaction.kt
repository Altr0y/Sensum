package si.sensum.backend.database

import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseTransaction {

    fun <T> run(block: () -> T): T {
        return transaction {
            block()
        }
    }
}