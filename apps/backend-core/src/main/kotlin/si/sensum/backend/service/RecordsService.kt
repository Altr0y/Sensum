package si.sensum.backend.service

import si.sensum.backend.domain.records.RecordsQuery
import si.sensum.backend.repository.RecordsRepository
import si.sensum.shared.models.records.ChannelRecordDto
import si.sensum.shared.models.records.MeasurementRecordDto
import si.sensum.shared.models.records.RecordsPageDto
import si.sensum.shared.models.records.StationRecordDto

class RecordsService(
    private val repository: RecordsRepository
) {
    fun getStations(query: RecordsQuery): RecordsPageDto<StationRecordDto> {
        return repository.findStations(query)
    }

    fun getChannels(query: RecordsQuery): RecordsPageDto<ChannelRecordDto> {
        return repository.findChannels(query)
    }

    fun getMeasurements(query: RecordsQuery): RecordsPageDto<MeasurementRecordDto> {
        return repository.findMeasurements(query)
    }
}