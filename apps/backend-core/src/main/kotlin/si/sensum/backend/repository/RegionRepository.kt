package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.RegionTable
import si.sensum.backend.domain.location.RegionEntity
import si.sensum.backend.mapper.toDataSourceDto

class RegionRepository {
    fun findAll(): List<RegionEntity> = DatabaseTransaction.run {
        RegionTable
            .selectAll()
            .map(::toRegion)
    }

    fun findById(regionId: Int): RegionEntity? = DatabaseTransaction.run {
        RegionTable
            .selectAll()
            .where { RegionTable.id eq regionId }
            .map(::toRegion)
            .singleOrNull()
    }

    fun findByCountryId(countryId: Int): List<RegionEntity> = DatabaseTransaction.run {
        RegionTable
            .selectAll()
            .where { RegionTable.countryId eq countryId }
            .map(::toRegion)
    }

    private fun toRegion(row: ResultRow): RegionEntity {
        return RegionEntity(
            id = row[RegionTable.id],
            countryId = row[RegionTable.countryId],
            name = row[RegionTable.name],
            geometry = row[RegionTable.geometry],
            source = row[RegionTable.dataSource].toDataSourceDto()
        )
    }
}