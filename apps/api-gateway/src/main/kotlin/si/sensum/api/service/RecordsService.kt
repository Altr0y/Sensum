package si.sensum.api.service

import si.sensum.api.client.BackendRecordsClient
import si.sensum.shared.models.records.ChannelRecordDto
import si.sensum.shared.models.records.MeasurementRecordDto
import si.sensum.shared.models.records.RecordsPageDto
import si.sensum.shared.models.records.StationRecordDto

internal class RecordsService(
    private val recordsClient: BackendRecordsClient
) {
    suspend fun getStations(queryString: String): RecordsPageDto<StationRecordDto> {
        return recordsClient.getStationRecords(queryString)
    }

    suspend fun getChannels(queryString: String): RecordsPageDto<ChannelRecordDto> {
        return recordsClient.getChannelRecords(queryString)
    }

    suspend fun getMeasurements(queryString: String): RecordsPageDto<MeasurementRecordDto> {
        return recordsClient.getMeasurementRecords(queryString)
    }
}