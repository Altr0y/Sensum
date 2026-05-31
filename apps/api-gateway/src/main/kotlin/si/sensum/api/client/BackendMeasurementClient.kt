package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.measurements.CreateMeasurementsBatchCommand
import si.sensum.shared.models.measurements.CreateMeasurementsBatchResult
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand
import si.sensum.shared.models.measurements.RefreshMeasurementsResult

internal class BackendMeasurementClient(
    private val backend: ServiceHttpClient
) {
    suspend fun getMeasurements(): List<MeasurementDto> {
        return backend.get("/api/v1/measurements")
    }

    suspend fun getMeasurementById(id: Long): MeasurementDto {
        return backend.get("/api/v1/measurements/$id")
    }

    suspend fun createMeasurement(request: MeasurementDto): MeasurementDto {
        return backend.post("/api/v1/measurements", request)
    }

    suspend fun createMeasurementsBatch(
        request: CreateMeasurementsBatchCommand
    ): CreateMeasurementsBatchResult {
        return backend.post("/api/v1/measurements/batch", request)
    }

    suspend fun updateMeasurement(
        id: Long,
        request: MeasurementDto
    ): MeasurementDto {
        return backend.put("/api/v1/measurements/$id", request)
    }

    suspend fun deleteMeasurement(id: Long) {
        backend.delete("/api/v1/measurements/$id")
    }

    suspend fun deleteAllMeasurements() {
        backend.delete("/api/v1/measurements")
    }

    suspend fun refreshMeasurements(
        request: RefreshMeasurementsCommand
    ): RefreshMeasurementsResult {
        return backend.post("/api/v1/measurements/refresh", request)
    }

    suspend fun getMeasurementsByRange(
        channelId: Int,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return backend.get(
            "/api/v1/measurements/range?channelId=$channelId&from=$datetimeFrom&to=$datetimeTo"
        )
    }

    suspend fun regenerateMeasurements(
        datetimeFrom: String,
        datetimeTo: String
    ): Map<String, Boolean> {
        return backend.post(
            path = "/api/v1/measurements/regenerate?from=$datetimeFrom&to=$datetimeTo",
            body = emptyMap<String, String>()
        )
    }

    suspend fun deleteMeasurementsByRange(
        datetimeFrom: String,
        datetimeTo: String
    ): Map<String, Int> {
        return backend.deleteWithResponse(
            "/api/v1/measurements/range?from=$datetimeFrom&to=$datetimeTo"
        )
    }
}