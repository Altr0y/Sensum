package si.sensum.demo.repository

import si.sensum.demo.model.Measurement

interface DatabaseRepository {
    suspend fun getAll(): Result<List<Measurement>>
    suspend fun insert(measurement: Measurement): Result<Measurement>
    suspend fun insertAll(measurements: List<Measurement>): Result<Int>
    suspend fun update(measurement: Measurement): Result<Measurement>
    suspend fun delete(id: Int): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}
