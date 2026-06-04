package si.sensum.backend.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.MunicipalityTable
import si.sensum.backend.domain.location.MunicipalityEntity
import si.sensum.backend.mapper.toDataSourceDto

class MunicipalityRepository {
    fun findAll(): List<MunicipalityEntity> = DatabaseTransaction.run {
        MunicipalityTable
            .selectAll()
            .map(::toMunicipality)
    }

    fun findById(municipalityId: Int): MunicipalityEntity? = DatabaseTransaction.run {
        MunicipalityTable
            .selectAll()
            .where { MunicipalityTable.id eq municipalityId }
            .map(::toMunicipality)
            .singleOrNull()
    }

    fun findByRegionId(regionId: Int): List<MunicipalityEntity> = DatabaseTransaction.run {
        MunicipalityTable
            .selectAll()
            .where { MunicipalityTable.regionId eq regionId }
            .map(::toMunicipality)
    }

    private fun toMunicipality(row: ResultRow): MunicipalityEntity {
        return MunicipalityEntity(
            id = row[MunicipalityTable.id],
            countryId = row[MunicipalityTable.countryId],
            regionId = row[MunicipalityTable.regionId],
            name = row[MunicipalityTable.name],
            geometry = row[MunicipalityTable.geometry],
            source = row[MunicipalityTable.dataSource].toDataSourceDto()
        )
    }
}

