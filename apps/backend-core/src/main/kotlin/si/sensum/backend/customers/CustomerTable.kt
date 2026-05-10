package si.sensum.backend.customers

import org.jetbrains.exposed.v1.core.Table

object CustomerTable : Table("customers") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}