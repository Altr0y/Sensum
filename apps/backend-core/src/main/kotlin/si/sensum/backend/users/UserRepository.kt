package si.sensum.backend.users

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.DatabaseTransaction

class UserRepository {

    fun createUser(
        username: String,
        passwordHash: String,
        role: UserRole
    ): User = DatabaseTransaction.run {
        val insertStatement = UserTable.insert {
            it[UserTable.username] = username
            it[UserTable.passwordHash] = passwordHash
            it[UserTable.role] = role
        }

        User(
            id = insertStatement[UserTable.id],
            username = username,
            passwordHash = passwordHash,
            role = role
        )
    }

    fun findByUsername(username: String): User? = DatabaseTransaction.run {
        UserTable
            .selectAll()
            .where { UserTable.username eq username }
            .map(::toUser)
            .singleOrNull()
    }

    fun findAll(): List<User> = DatabaseTransaction.run {
        UserTable
            .selectAll()
            .map(::toUser)
    }

    private fun toUser(row: ResultRow): User {
        return User(
            id = row[UserTable.id],
            username = row[UserTable.username],
            passwordHash = row[UserTable.passwordHash],
            role = row[UserTable.role]
        )
    }
}