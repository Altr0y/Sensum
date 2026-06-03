package si.sensum.shared.ktor.validation

import io.ktor.server.application.ApplicationCall
import si.sensum.shared.ktor.errors.InvalidRequiredFieldException
import si.sensum.shared.ktor.errors.MissingRequiredFieldException

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

fun ApplicationCall.requireStringQueryParameter(name: String): String {
    val value = request.queryParameters[name]
        ?: throw MissingRequiredFieldException(name)

    return value.takeIf { it.isNotBlank() }
        ?: throw InvalidRequiredFieldException(name, value)
}

fun ApplicationCall.requireIntQueryParameter(name: String): Int {
    val value = requireStringQueryParameter(name)

    return value.toIntOrNull()
        ?: throw InvalidRequiredFieldException(name, value)
}

fun ApplicationCall.requireLongQueryParameter(name: String): Long {
    val value = requireStringQueryParameter(name)

    return value.toLongOrNull()
        ?: throw InvalidRequiredFieldException(name, value)
}