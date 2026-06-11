package si.sensum.api.service

import si.sensum.api.client.BackendDslClient
import si.sensum.shared.models.dsl.DslProcessResult

internal class DslService(
    private val dslClient: BackendDslClient
) {
    suspend fun processSource(source: String): DslProcessResult {
        return dslClient.processSource(source)
    }
}
