package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.data.DataImportCommand
import si.sensum.shared.models.data.DataImportResult
import si.sensum.shared.models.data.DslImportCommand
import si.sensum.shared.models.data.SwsImportCommand

internal class BackendDataImportClient(
    private val backend: ServiceHttpClient
) {
    suspend fun importManual(command: DataImportCommand): DataImportResult {
        return backend.post(
            path = "/api/v1/data/import/manual",
            body = command
        )
    }

    suspend fun importSimulation(command: DataImportCommand): DataImportResult {
        return backend.post(
            path = "/api/v1/data/import/simulation",
            body = command
        )
    }

    suspend fun importDsl(command: DslImportCommand): DataImportResult {
        return backend.post(
            path = "/api/v1/data/import/dsl",
            body = command
        )
    }

    suspend fun importSwsStations(command: SwsImportCommand): DataImportResult {
        return backend.post(
            path = "/api/v1/data/import/sws/stations",
            body = command
        )
    }

    suspend fun importSwsChannels(command: SwsImportCommand): DataImportResult {
        return backend.post(
            path = "/api/v1/data/import/sws/channels",
            body = command
        )
    }

    suspend fun importSwsMeasurements(command: SwsImportCommand): DataImportResult {
        return backend.post(
            path = "/api/v1/data/import/sws/measurements",
            body = command
        )
    }
}