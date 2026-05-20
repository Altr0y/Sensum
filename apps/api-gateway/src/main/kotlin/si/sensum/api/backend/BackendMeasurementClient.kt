package si.sensum.api.backend

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsResponse

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
        request: MeasurementsByStationChannelPairsRequest
    ): MeasurementsByStationChannelPairsResponse {
        return backend.post("/api/v1/measurements/refresh", request)
    }
}