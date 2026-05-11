package si.sensum.backend.users

import org.jetbrains.exposed.v1.core.Table

object UserTable : Table("users") {
    val id = integer("id").autoIncrement()
    val customerId = integer("customer_id")
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)

    val role = enumerationByName<UserRole>(
        name = "role",
        length = 10
    )

    override val primaryKey = PrimaryKey(id)
}