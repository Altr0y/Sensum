package si.sensum.sws

import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import si.sensum.logging.Logger

internal object SwsSoapResponseValidator {

    private val log = Logger.log

    fun validate(
        status: HttpStatusCode,
        operationName: String
    ) {
        if (status == HttpStatusCode.Unauthorized) {
            log.warn { "[SWS] $operationName failed: unauthorized" }
            throw SwsUnauthorizedException("SWS session is unauthorized or expired")
        }

        if (!status.isSuccess()) {
            log.error { "[SWS] $operationName failed: HTTP ${status.value}" }
            throw SwsHttpException(
                statusCode = status.value,
                message = "SWS $operationName failed with HTTP ${status.value}"
            )
        }
    }
}