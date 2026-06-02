package si.sensum.demo.repository

import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.model.MeasurementRequestUi

interface MeasurementRepository {
    suspend fun getMeasurements(request: MeasurementRequestUi): Result<List<MeasurementUi>>
    suspend fun regenerateAndGet(
        request: MeasurementRequestUi
    ): Result<List<MeasurementUi>> {
        return getMeasurements(request)
    }
}
