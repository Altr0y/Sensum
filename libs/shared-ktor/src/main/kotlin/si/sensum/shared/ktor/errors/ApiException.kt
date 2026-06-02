package si.sensum.shared.ktor.errors

import io.ktor.http.HttpStatusCode

open class ApiException(
    val statusCode: HttpStatusCode,
    override val message: String
) : RuntimeException(message)

class MissingRequiredFieldException(
    fieldName: String
) : ApiException(
    statusCode = HttpStatusCode.BadRequest,
    message = "Missing required field: $fieldName"
)

class InvalidRequiredFieldException(
    fieldName: String,
    value: String
) : ApiException(
    statusCode = HttpStatusCode.BadRequest,
    message = "Invalid value for required field '$fieldName': $value"
)