package si.sensum.backend.service

import si.sensum.logging.Logger
import kotlin.system.measureTimeMillis

internal object ServiceLogger {
    fun <T> call(
        service: String,
        operation: String,
        details: String? = null,
        block: () -> T
    ): T {
        val prefix = buildPrefix(service, operation, details)

        Logger.log.info {
            "$prefix started"
        }

        var result: Result<T>? = null

        val duration = measureTimeMillis {
            result = runCatching {
                block()
            }
        }

        val finalResult = result
            ?: error("Service result was not initialized")

        finalResult
            .onSuccess {
                Logger.log.info {
                    "$prefix completed duration=${duration}ms"
                }
            }
            .onFailure { error ->
                Logger.log.warn {
                    "$prefix failed duration=${duration}ms message=${error.message ?: error::class.simpleName}"
                }
            }

        return finalResult.getOrThrow()
    }

    suspend fun <T> suspendCall(
        service: String,
        operation: String,
        details: String? = null,
        block: suspend () -> T
    ): T {
        val prefix = buildPrefix(service, operation, details)

        Logger.log.info {
            "$prefix started"
        }

        var result: Result<T>? = null

        val duration = measureTimeMillis {
            result = runCatching {
                block()
            }
        }

        val finalResult = result
            ?: error("Service result was not initialized")

        finalResult
            .onSuccess {
                Logger.log.info {
                    "$prefix completed duration=${duration}ms"
                }
            }
            .onFailure { error ->
                Logger.log.warn {
                    "$prefix failed duration=${duration}ms message=${error.message ?: error::class.simpleName}"
                }
            }

        return finalResult.getOrThrow()
    }

    private fun buildPrefix(
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
}