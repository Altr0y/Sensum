package si.sensum.api.service

import si.sensum.api.client.BackendDataImportClient
import si.sensum.api.domain.ApiPrincipal
import si.sensum.shared.models.common.DataSourceDto
import si.sensum.shared.models.data.DataImportCommand
import si.sensum.shared.models.data.DataImportResult
import si.sensum.shared.models.data.DslImportCommand
import si.sensum.shared.models.data.SwsImportCommand

internal class DataImportService(
    private val client: BackendDataImportClient
) {
    suspend fun importManual(
        principal: ApiPrincipal,
        command: DataImportCommand
    ): DataImportResult {
        return client.importManual(
            command.withPrincipal(
                principal = principal,
                forcedSource = DataSourceDto.MANUAL
            )
        )
    }

    suspend fun importSimulation(
        principal: ApiPrincipal,
        command: DataImportCommand
    ): DataImportResult {
        return client.importSimulation(
            command.withPrincipal(
                principal = principal,
                forcedSource = DataSourceDto.SIM
            )
        )
    }

    suspend fun importDsl(
        principal: ApiPrincipal,
        source: String
    ): DataImportResult {
        return client.importDsl(
            DslImportCommand(
                userId = principal.userId,
                customerId = principal.customerId,
                source = source
            )
        )
    }

    suspend fun importSwsStations(
        principal: ApiPrincipal,
        command: SwsImportCommand
    ): DataImportResult {
        return client.importSwsStations(
            command.withPrincipal(principal)
        )
    }

    suspend fun importSwsChannels(
        principal: ApiPrincipal,
        command: SwsImportCommand
    ): DataImportResult {
        return client.importSwsChannels(
            command.withPrincipal(principal)
        )
    }

    suspend fun importSwsMeasurements(
        principal: ApiPrincipal,
        command: SwsImportCommand
    ): DataImportResult {
        return client.importSwsMeasurements(
            command.withPrincipal(principal)
        )
    }

    private fun DataImportCommand.withPrincipal(
        principal: ApiPrincipal,
        forcedSource: DataSourceDto
    ): DataImportCommand {
        return copy(
            userId = principal.userId,
            customerId = principal.customerId,
            source = forcedSource
        )
    }

    private fun SwsImportCommand.withPrincipal(
        principal: ApiPrincipal
    ): SwsImportCommand {
        return copy(
            userId = principal.userId,
            customerId = principal.customerId
        )
    }
}