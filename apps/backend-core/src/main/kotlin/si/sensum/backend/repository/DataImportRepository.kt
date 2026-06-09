package si.sensum.backend.repository

import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import si.sensum.backend.database.ChannelTable
import si.sensum.backend.database.DatabaseTransaction
import si.sensum.backend.database.MeasurementTable
import si.sensum.backend.database.StationTable
import si.sensum.backend.database.UserStationTable
import si.sensum.backend.domain.measurement.MeasurementUnit
import si.sensum.backend.domain.station.Permission
import si.sensum.backend.mapper.toDbValue
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.shared.models.common.DataSourceDto
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.stations.StationDto

class DataImportRepository {

    fun upsertAll(
        userId: Int,
        customerId: Int,
        source: DataSourceDto,
        stations: List<StationDto>,
        channels: List<ChannelDto>,
        measurements: List<MeasurementDto>
    ): ImportCounts = DatabaseTransaction.run {
        var stationCount = 0
        var channelCount = 0
        var measurementCount = 0
        var userStationCount = 0

        stations.forEach { station ->
            upsertStation(
                customerId = customerId,
                dto = station.copy(source = source)
            )
            stationCount++

            if (insertUserStationIfMissing(userId, station.stationId)) {
                userStationCount++
            }
        }

        channels.forEach { channel ->
            upsertChannel(channel.copy(source = source))
            channelCount++
        }

        measurements.forEach { measurement ->
            upsertMeasurement(measurement.copy(source = source))
            measurementCount++
        }

        ImportCounts(
            stationCount = stationCount,
            channelCount = channelCount,
            measurementCount = measurementCount,
            userStationCount = userStationCount
        )
    }

    private fun upsertStation(
        customerId: Int,
        dto: StationDto
    ) {
        val existing = StationTable
            .selectAll()
            .where { StationTable.id eq dto.stationId }
            .singleOrNull()

        if (existing == null) {
            StationTable.insert {
                it[id] = dto.stationId
                it[StationTable.customerId] = customerId
                it[countryId] = dto.countryId
                it[regionId] = dto.regionId
                it[municipalityId] = dto.municipalityId
                it[alias] = dto.name ?: "Station ${dto.stationId}"
                it[serialNumber] = dto.serialNumber ?: dto.stationId.toString()
                it[locationDescription] = dto.description ?: "Imported ${dto.source.name} station"
                it[longitude] = dto.longitude ?: 0.0
                it[latitude] = dto.latitude ?: 0.0
                it[location] = "${dto.latitude ?: 0.0},${dto.longitude ?: 0.0}"
                it[dataSource] = dto.source.toDbValue()
            }
        } else {
            StationTable.update({ StationTable.id eq dto.stationId }) {
                it[StationTable.customerId] = customerId
                it[countryId] = dto.countryId
                it[regionId] = dto.regionId
                it[municipalityId] = dto.municipalityId
                it[alias] = dto.name ?: existing[StationTable.alias]
                it[serialNumber] = dto.serialNumber ?: existing[StationTable.serialNumber]
                it[locationDescription] = dto.description ?: existing[StationTable.locationDescription]
                it[longitude] = dto.longitude ?: existing[StationTable.longitude]
                it[latitude] = dto.latitude ?: existing[StationTable.latitude]
                it[location] = "${dto.latitude ?: existing[StationTable.latitude]},${dto.longitude ?: existing[StationTable.longitude]}"
                it[dataSource] = dto.source.toDbValue()
            }
        }
    }

    private fun upsertChannel(dto: ChannelDto) {
        val existing = ChannelTable
            .selectAll()
            .where { ChannelTable.id eq dto.channelId }
            .singleOrNull()

        if (existing == null) {
            ChannelTable.insert {
                it[id] = dto.channelId
                it[stationId] = dto.stationId
                it[name] = dto.name ?: "Channel ${dto.channelId}"
                it[description] = dto.description ?: "Imported ${dto.source.name} channel"
                it[unit] = dto.unit.toMeasurementUnit()
                it[enabled] = true
                it[dataSource] = dto.source.toDbValue()
            }
        } else {
            ChannelTable.update({ ChannelTable.id eq dto.channelId }) {
                it[stationId] = dto.stationId
                it[name] = dto.name ?: existing[ChannelTable.name]
                it[description] = dto.description ?: existing[ChannelTable.description]
                it[unit] = dto.unit.toMeasurementUnit()
                it[enabled] = true
                it[dataSource] = dto.source.toDbValue()
            }
        }
    }

    private fun upsertMeasurement(dto: MeasurementDto) {
        val localDateTime = ApiDateTime
            .toAppLocal(dto.dateTime)
            .toKotlinLocalDateTime()

        val existing = MeasurementTable
            .selectAll()
            .where {
                (MeasurementTable.channelId eq dto.channelId) and
                        (MeasurementTable.dateTime eq localDateTime) and
                        (MeasurementTable.dataSource eq dto.source.toDbValue())
            }
            .singleOrNull()

        if (existing == null) {
            MeasurementTable.insert {
                it[channelId] = dto.channelId
                it[dateTime] = localDateTime
                it[value] = dto.value.toFloat()
                it[status] = dto.status != 0
                it[dataSource] = dto.source.toDbValue()
            }
        } else {
            MeasurementTable.update({ MeasurementTable.id eq existing[MeasurementTable.id] }) {
                it[value] = dto.value.toFloat()
                it[status] = dto.status != 0
                it[dataSource] = dto.source.toDbValue()
            }
        }
    }

    private fun insertUserStationIfMissing(
        userId: Int,
        stationId: Long
    ): Boolean {
        val exists = UserStationTable
            .selectAll()
            .where {
                (UserStationTable.userId eq userId) and
                        (UserStationTable.stationId eq stationId)
            }
            .any()

        if (exists) {
            return false
        }

        UserStationTable.insert {
            it[UserStationTable.userId] = userId
            it[UserStationTable.stationId] = stationId
            it[permission] = Permission.ADMIN
        }

        return true
    }

    private fun String?.toMeasurementUnit(): MeasurementUnit {
        val normalized = orEmpty().trim().lowercase()

        return when (normalized) {
            in listOf("m", "meter", "meters") -> MeasurementUnit.METERS
            in listOf("c", "°c", "celsius") -> MeasurementUnit.CELSIUS
            in listOf("%", "percent") -> MeasurementUnit.PERCENT
            in listOf("v", "volt") -> MeasurementUnit.VOLT
            else -> MeasurementUnit.UNKNOWN
        }
    }
}

data class ImportCounts(
    val stationCount: Int,
    val channelCount: Int,
    val measurementCount: Int,
    val userStationCount: Int
)