package si.sensum.shared.ktor.validation

import io.ktor.server.application.ApplicationCall
import si.sensum.shared.ktor.errors.MissingRequiredFieldException
import si.sensum.shared.ktor.errors.InvalidRequiredFieldException

fun ApplicationCall.requireLongPathParameter(name: String): Long {
    val value = parameters[name]
        ?: throw MissingRequiredFieldException(name)

    return value.toLongOrNull()
        ?: throw InvalidRequiredFieldException(name, value)
}

fun ApplicationCall.requireIntPathParameter(name: String): Int {
    val value = parameters[name]
        ?: throw MissingRequiredFieldException(name)

    return value.toIntOrNull()
        ?: throw InvalidRequiredFieldException(name, value)
}