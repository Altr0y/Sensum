package si.sensum.gm.errors

import io.ktor.http.HttpStatusCode

internal open class GmApiException(
    val statusCode: HttpStatusCode,
    override val message: String
) : RuntimeException(message)

internal class MissingRequiredFieldException(
    fieldName: String
) : GmApiException(
    statusCode = HttpStatusCode.BadRequest,
    message = "Missing required field: $fieldName"
)

internal class InvalidRequiredFieldException(
    fieldName: String,
    reason: String
) : GmApiException(
    statusCode = HttpStatusCode.BadRequest,
    message = "Invalid field '$fieldName': $reason"
)