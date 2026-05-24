package si.sensum.backend.users

import org.jetbrains.exposed.v1.core.Table

object UserTable : Table("users") {
    val id = integer("id").autoIncrement()
    val customerId = integer("customer_id").references(si.sensum.backend.customers.CustomerTable.id)
    val username = varchar("username", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = enumerationByName<UserRole>("role", 20)
    val enabled = bool("enabled").default(true)

    override val primaryKey = PrimaryKey(id)
}