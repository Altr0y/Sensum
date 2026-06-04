package si.sensum.backend.database

import org.jetbrains.exposed.v1.core.Table

object CountryTable : Table("countries") {
    val id = integer("id").autoIncrement()
    val code = integer("code").uniqueIndex()
    val name = varchar("name", 50)
    val originName = varchar("originName", 500).nullable()
    val geometry = text("geometry").nullable()

    val dataSource = varchar("source", 20).default("UNKNOWN")

    override val primaryKey = PrimaryKey(id)
}