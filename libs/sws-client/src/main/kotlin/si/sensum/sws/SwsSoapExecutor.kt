package si.sensum.sws

import io.ktor.client.*
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import si.sensum.logging.Logger
import si.sensum.sws.model.SwsSession
import si.sensum.sws.model.SwsSoapOperation
import si.sensum.sws.model.SwsSoapResponse
import si.sensum.sws.model.asCookieHeader

internal class SwsSoapExecutor(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    private val log = Logger.log

    suspend fun execute(
        operation: SwsSoapOperation,
        session: SwsSession? = null
    ): String {
        return executeWithHeaders(
            operation = operation,
            session = session
        ).body
    }

    suspend fun executeWithHeaders(
        operation: SwsSoapOperation,
        session: SwsSession? = null
    ): SwsSoapResponse {
        logRequest(operation)

        val httpResponse = sendRequest(
            operation = operation,
            session = session
        )

        val responseBody = readResponseBody(
            operation = operation,
            response = httpResponse
        )

        SwsSoapResponseValidator.validate(
            status = httpResponse.status,
            operationName = operation.name,
            responseBody = responseBody
        )

        return SwsSoapResponse(
            body = responseBody,
            headers = httpResponse.headers
        )
    }

    private suspend fun sendRequest(
        operation: SwsSoapOperation,
        session: SwsSession?
    ): HttpResponse {
        return try {
            httpClient.post(baseUrl) {
                applySoapHeaders(
                    action = operation.action,
                    soapVersion = operation.soapVersion
                )

                session?.let {
                    header(HttpHeaders.Cookie, it.asCookieHeader())
                }

                setBody(operation.xmlEnvelope)
            }
        } catch (error: HttpRequestTimeoutException) {
            log.error {
                "[SWS] ${operation.name} timed out url=$baseUrl message=${error.message}"
            }

            throw SwsTimeoutException(
                message = "SWS ${operation.name} timed out"
            )
        }
    }

    private suspend fun readResponseBody(
        operation: SwsSoapOperation,
        response: HttpResponse
    ): String {
        val responseBody = response.bodyAsText()

        logResponse(
            operation = operation,
            response = response,
            responseBody = responseBody
        )

        return responseBody
    }

    private fun logRequest(operation: SwsSoapOperation) {
        log.info { "[SWS] ${operation.name} request -> $baseUrl" }
        log.debug { "[SWS] ${operation.name} request envelopeLength=${operation.xmlEnvelope.length}" }
    }

    private fun logResponse(
        operation: SwsSoapOperation,
        response: HttpResponse,
        responseBody: String
    ) {
        log.info { "[SWS] ${operation.name} response <- status=${response.status.value}" }
        log.debug { "[SWS] ${operation.name} response bodyLength=${responseBody.length}" }
    }
}