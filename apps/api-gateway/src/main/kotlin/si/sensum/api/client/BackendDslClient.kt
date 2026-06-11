package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.dsl.DslProcessRequest
import si.sensum.shared.models.dsl.DslProcessResult

internal class BackendDslClient(
    private val backend: ServiceHttpClient
) {
    suspend fun processSource(source: String): DslProcessResult {
        return backend.post("/api/v1/dsl/process", DslProcessRequest(source = source))
    }
}
