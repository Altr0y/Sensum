package si.sensum.gm.services

import si.sensum.logging.loggedServiceCall

internal abstract class GmService(
    private val serviceName: String
) {
    protected suspend fun <T> logged(
        operation: String,
        details: String? = null,
        block: suspend () -> T
    ): T {
        return loggedServiceCall(
            service = serviceName,
            operation = operation,
            details = details,
            block = block
        )
    }
}