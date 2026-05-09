package si.sensum.demo.repository

import si.sensum.demo.model.Measurement
import si.sensum.demo.model.MeasurementRequest

interface MeasurementRepository {
    suspend fun getMeasurements(request: MeasurementRequest): Result<List<Measurement>>
}
