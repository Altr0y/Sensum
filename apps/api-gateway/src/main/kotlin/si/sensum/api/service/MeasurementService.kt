package si.sensum.api.service

import si.sensum.api.client.BackendMeasurementClient
import si.sensum.shared.models.measurements.CreateMeasurementsBatchCommand
import si.sensum.shared.models.measurements.CreateMeasurementsBatchResult
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand
import si.sensum.shared.models.measurements.RefreshMeasurementsResult

internal class MeasurementService(
    private val measurements: BackendMeasurementClient
) {
    suspend fun getMeasurements(): List<MeasurementDto> {
        return measurements.getMeasurements()
    }

    suspend fun getMeasurementById(id: Long): MeasurementDto {
        require(id > 0) {
            "Measurement id must be positive"
        }

        return measurements.getMeasurementById(id)
    }

    suspend fun createMeasurement(
        request: MeasurementDto
    ): MeasurementDto {
        return measurements.createMeasurement(request)
    }

    suspend fun createMeasurementsBatch(
        request: CreateMeasurementsBatchCommand
    ): CreateMeasurementsBatchResult {
        require(request.measurements.isNotEmpty()) {
            "Measurements batch must not be empty"
        }

        return measurements.createMeasurementsBatch(request)
    }

    suspend fun updateMeasurement(
        id: Long,
        request: MeasurementDto
    ): MeasurementDto {
        require(id > 0) {
            "Measurement id must be positive"
        }

        return measurements.updateMeasurement(
            id = id,
            request = request
        )
    }

    suspend fun deleteMeasurement(id: Long) {
        require(id > 0) {
            "Measurement id must be positive"
        }

        measurements.deleteMeasurement(id)
    }

    suspend fun deleteAllMeasurements() {
        measurements.deleteAllMeasurements()
    }

    suspend fun refreshMeasurements(
        request: RefreshMeasurementsCommand
    ): RefreshMeasurementsResult {
        require(request.stationChannelPairs.isNotEmpty()) {
            "At least one station/channel pair is required"
        }

        require(request.datetimeFrom.isNotBlank()) {
            "datetimeFrom must not be blank"
        }

        require(request.datetimeTo.isNotBlank()) {
            "datetimeTo must not be blank"
        }

        return measurements.refreshMeasurements(request)
    }

    suspend fun getMeasurementsByRange(
        channelId: Int,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        require(channelId > 0) {
            "Channel id must be positive"
        }

        require(datetimeFrom.isNotBlank()) {
            "from must not be blank"
        }

        require(datetimeTo.isNotBlank()) {
            "to must not be blank"
        }

        return measurements.getMeasurementsByRange(
            channelId = channelId,
            datetimeFrom = datetimeFrom,
            datetimeTo = datetimeTo
        )
    }

    suspend fun regenerateMeasurements(
        datetimeFrom: String,
        datetimeTo: String
    ): Map<String, Boolean> {
        require(datetimeFrom.isNotBlank()) {
            "from must not be blank"
        }

        require(datetimeTo.isNotBlank()) {
            "to must not be blank"
        }

        return measurements.regenerateMeasurements(
            datetimeFrom = datetimeFrom,
            datetimeTo = datetimeTo
        )
    }

    suspend fun deleteMeasurementsByRange(
        datetimeFrom: String,
        datetimeTo: String
    ): Map<String, Int> {
        require(datetimeFrom.isNotBlank()) {
            "from must not be blank"
        }

        require(datetimeTo.isNotBlank()) {
            "to must not be blank"
        }

        return measurements.deleteMeasurementsByRange(
            datetimeFrom = datetimeFrom,
            datetimeTo = datetimeTo
        )
    }
}