package si.sensum.gm.services

import si.sensum.logging.loggedCall
import si.sensum.logging.loggedSuspendCall

internal object ServiceLogger {
    fun <T> call(
        service: String,
        operation: String,
        details: String? = null,
        block: () -> T
    ): T {
        return loggedCall(
            service = "eltratec-gm.$service",
            operation = operation,
            details = details,
            block = block
        )
    }

    suspend fun <T> suspendCall(
        service: String,
        operation: String,
        details: String? = null,
        block: suspend () -> T
    ): T {
        return loggedSuspendCall(
            service = "eltratec-gm.$service",
            operation = operation,
            details = details,
            block = block
        )
    }
}