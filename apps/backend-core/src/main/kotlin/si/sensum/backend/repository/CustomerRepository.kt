package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import si.sensum.backend.database.CustomerTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.domain.customer.CustomerEntity

class CustomerRepository {


    fun findById(customerId: Int): CustomerEntity? = DatabaseTransaction.run {
        CustomerTable
            .selectAll()
            .where { CustomerTable.id eq customerId }
            .map(::toCustomer)
            .singleOrNull()
    }

    fun create(name: String): CustomerEntity {
        val id = DatabaseTransaction.run {
            CustomerTable.insert {
                it[CustomerTable.name] = name
            } get CustomerTable.id
        }

        return findById(id)
            ?: error("Created customer was not found")
    }

    fun update(
        customerId: Int,
        name: String
    ): CustomerEntity? {
        DatabaseTransaction.run {
            CustomerTable.update(
                where = { CustomerTable.id eq customerId }
            ) {
                it[CustomerTable.name] = name
            }
        }

        return findById(customerId)
    }

    private fun toCustomer(row: ResultRow): CustomerEntity {
        return CustomerEntity(
            id = row[CustomerTable.id],
            name = row[CustomerTable.name]
        )
    }
}