package si.sensum.sws

import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import si.sensum.logging.Logger

internal object SwsSoapResponseValidator {

    private val log = Logger.log

    private val SOAP_FAULT_REGEX = Regex(
        pattern = "<(?:\\w+:)?Fault[\\s>]",
        options = setOf(RegexOption.IGNORE_CASE)
    )

    fun validate(
        status: HttpStatusCode,
        operationName: String,
        responseBody: String
    ) {
        validateHttpStatus(
            status = status,
            operationName = operationName
        )

        validateResponseBody(
            operationName = operationName,
            responseBody = responseBody
        )
    }

    private fun validateHttpStatus(
        status: HttpStatusCode,
        operationName: String
    ) {
        if (status.isSuccess()) {
            return
        }

        if (status == HttpStatusCode.Unauthorized) {
            val message = "SWS session is unauthorized or expired"

            log.warn { "[SWS] $operationName failed: $message" }

            throw SwsUnauthorizedException(message)
        }

        val message = swsHttpErrorMessage(
            operationName = operationName,
            status = status
        )

        log.error { "[SWS] $operationName failed: $message" }

        throw SwsHttpException(
            statusCode = status.value,
            message = message
        )
    }

    private fun validateResponseBody(
        operationName: String,
        responseBody: String
    ) {
        if (responseBody.isBlank()) {
            val message = "SWS $operationName returned empty response body"

            log.error { "[SWS] $operationName failed: empty response body" }

            throw SwsInvalidResponseException(message)
        }

        if (SOAP_FAULT_REGEX.containsMatchIn(responseBody)) {
            val message = "SWS $operationName returned SOAP Fault"

            log.error { "[SWS] $operationName failed: SOAP Fault returned" }

            throw SwsInvalidResponseException(message)
        }
    }

    private fun swsHttpErrorMessage(
        operationName: String,
        status: HttpStatusCode
    ): String {
        return "SWS $operationName failed with HTTP ${status.value} ${status.description}"
    }
}