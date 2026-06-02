package si.sensum.gm.validation

import io.ktor.server.application.ApplicationCall
import si.sensum.gm.errors.InvalidRequiredFieldException
import si.sensum.gm.errors.MissingRequiredFieldException

internal fun ApplicationCall.requireLongPathParameter(name: String): Long {
    val value = parameters[name]

    if (value.isNullOrBlank()) {
        throw MissingRequiredFieldException(name)
    }

    return value.toLongOrNull()
        ?: throw InvalidRequiredFieldException(name, "expected long")
}

internal fun ApplicationCall.requireIntPathParameter(name: String): Int {
    val value = parameters[name]

    if (value.isNullOrBlank()) {
        throw MissingRequiredFieldException(name)
    }

    return value.toIntOrNull()
        ?: throw InvalidRequiredFieldException(name, "expected int")
}