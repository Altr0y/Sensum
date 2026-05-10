package si.sensum.gm.logging

import si.sensum.logging.Logger
import kotlin.system.measureTimeMillis

suspend fun <T> loggedServiceCall(
    service: String,
    operation: String,
    details: String? = null,
    block: suspend () -> T
): T {
    val logPrefix = buildString {
        append("[$service] $operation")

        if (!details.isNullOrBlank()) {
            append(" ")
            append(details)
        }
    }

    Logger.log.info {
        "$logPrefix started"
    }

    var result: Result<T>? = null

    val duration = measureTimeMillis {
        result = runCatching {
            block()
        }
    }

    val finalResult = result
        ?: error("Service call result was not initialized")

    return finalResult
        .onSuccess {
            Logger.log.info {
                "$logPrefix completed duration=${duration}ms"
            }
        }
        .onFailure { error ->
            Logger.log.error {
                "$logPrefix failed duration=${duration}ms message=${error.message ?: error::class.simpleName}"
            }
        }
        .getOrThrow()
}