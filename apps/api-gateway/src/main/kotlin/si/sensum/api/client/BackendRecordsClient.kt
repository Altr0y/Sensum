package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.records.ChannelRecordDto
import si.sensum.shared.models.records.MeasurementRecordDto
import si.sensum.shared.models.records.RecordsPageDto
import si.sensum.shared.models.records.StationRecordDto

internal class BackendRecordsClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getStationRecords(queryString: String): RecordsPageDto<StationRecordDto> {
        return backend.get("/api/v1/records/stations$queryString")
    }

    suspend fun getChannelRecords(queryString: String): RecordsPageDto<ChannelRecordDto> {
        return backend.get("/api/v1/records/channels$queryString")
    }

    suspend fun getMeasurementRecords(queryString: String): RecordsPageDto<MeasurementRecordDto> {
        return backend.get("/api/v1/records/measurements$queryString")
    }
}