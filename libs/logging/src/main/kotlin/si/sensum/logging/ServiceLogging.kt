package si.sensum.logging

import kotlin.system.measureTimeMillis

fun <T> loggedCall(
    service: String,
    operation: String,
    details: String? = null,
    block: () -> T
): T {
    val logPrefix = buildServiceLogPrefix(
        service = service,
        operation = operation,
        details = details
    )

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

    finalResult
        .onSuccess {
            Logger.log.info {
                "$logPrefix completed duration=${duration}ms"
            }
        }
        .onFailure { error ->
            Logger.log.warn(error) {
                "$logPrefix failed duration=${duration}ms message=${error.message ?: error::class.simpleName}"
            }
        }

    return finalResult.getOrThrow()
}

suspend fun <T> loggedSuspendCall(
    service: String,
    operation: String,
    details: String? = null,
    block: suspend () -> T
): T {
    val logPrefix = buildServiceLogPrefix(
        service = service,
        operation = operation,
        details = details
    )

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

    finalResult
        .onSuccess {
            Logger.log.info {
                "$logPrefix completed duration=${duration}ms"
            }
        }
        .onFailure { error ->
            Logger.log.warn(error) {
                "$logPrefix failed duration=${duration}ms message=${error.message ?: error::class.simpleName}"
            }
        }

    return finalResult.getOrThrow()
}

/**
 * Backward-compatible alias, ker ga lahko katera obstoječa koda še kliče.
 */
suspend fun <T> loggedServiceCall(
    service: String,
    operation: String,
    details: String? = null,
    block: suspend () -> T
): T {
    return loggedSuspendCall(
        service = service,
        operation = operation,
        details = details,
        block = block
    )
}

private fun buildServiceLogPrefix(
    service: String,
    operation: String,
    details: String?
): String {
    return buildString {
        append("[SERVICE] service=")
        append(service)
        append(" operation=")
        append(operation)

        if (!details.isNullOrBlank()) {
            append(" ")
            append(details)
        }
    }
}