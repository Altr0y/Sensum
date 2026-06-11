package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.CountryTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.domain.location.CountryEntity
import si.sensum.backend.mapper.toDataSourceDto

class CountryRepository {
    fun findAll(): List<CountryEntity> = DatabaseTransaction.run {
        CountryTable
            .selectAll()
            .map(::toCountry)
    }

    fun findById(countryId: Int): CountryEntity? = DatabaseTransaction.run {
        CountryTable
            .selectAll()
            .where { CountryTable.id eq countryId }
            .map(::toCountry)
            .singleOrNull()
    }

    private fun toCountry(row: ResultRow): CountryEntity {
        return CountryEntity(
            id = row[CountryTable.id],
            code = row[CountryTable.code],
            name = row[CountryTable.name],
            originName = row[CountryTable.originName],
            geometry = row[CountryTable.geometry],
            source = row[CountryTable.dataSource].toDataSourceDto()
        )
    }
}