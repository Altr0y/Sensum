package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.UserTable
import si.sensum.backend.domain.user.UserEntity
import si.sensum.backend.domain.user.UserRole

class UserRepository {

    fun findByUsername(username: String): UserEntity? = DatabaseTransaction.run {
        UserTable
            .selectAll()
            .where { UserTable.username eq username }
            .map(::toUser)
            .singleOrNull()
    }

    fun findByCustomerId(customerId: Int): List<UserEntity> = DatabaseTransaction.run {
        UserTable
            .selectAll()
            .where { UserTable.customerId eq customerId }
            .map(::toUser)
    }

    fun findByCustomerAndId(
        customerId: Int,
        userId: Int
    ): UserEntity? = DatabaseTransaction.run {
        UserTable
            .selectAll()
            .where {
                (UserTable.customerId eq customerId) and
                        (UserTable.id eq userId)
            }
            .map(::toUser)
            .singleOrNull()
    }

    fun create(
        customerId: Int,
        username: String,
        passwordHash: String,
        role: UserRole,
        enabled: Boolean
    ): UserEntity {
        val id = DatabaseTransaction.run {
            UserTable.insert {
                it[UserTable.customerId] = customerId
                it[UserTable.username] = username
                it[UserTable.passwordHash] = passwordHash
                it[UserTable.role] = role
                it[UserTable.enabled] = enabled
            } get UserTable.id
        }

        return findByCustomerAndId(customerId, id)
            ?: error("Created user was not found")
    }

    fun update(
        customerId: Int,
        userId: Int,
        username: String?,
        passwordHash: String?,
        role: UserRole?,
        enabled: Boolean?
    ): UserEntity? {
        DatabaseTransaction.run {
            UserTable.update(
                where = {
                    (UserTable.customerId eq customerId) and
                            (UserTable.id eq userId)
                }
            ) {
                username?.let { value -> it[UserTable.username] = value }
                passwordHash?.let { value -> it[UserTable.passwordHash] = value }
                role?.let { value -> it[UserTable.role] = value }
                enabled?.let { value -> it[UserTable.enabled] = value }
            }
        }

        return findByCustomerAndId(customerId, userId)
    }

    fun delete(
        customerId: Int,
        userId: Int
    ): Boolean {
        val deletedCount = DatabaseTransaction.run {
            UserTable.deleteWhere {
                (UserTable.customerId eq customerId) and
                        (UserTable.id eq userId)
            }
        }

        return deletedCount > 0
    }

    private fun toUser(row: ResultRow): UserEntity {
        return UserEntity(
            id = row[UserTable.id],
            customerId = row[UserTable.customerId],
            username = row[UserTable.username],
            passwordHash = row[UserTable.passwordHash],
            role = row[UserTable.role],
            enabled = row[UserTable.enabled]
        )
    }
}