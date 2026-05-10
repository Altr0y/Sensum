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
        log.info { "[SWS] ${operation.name} request -> $baseUrl" }
        log.debug { "[SWS] ${operation.name} request envelopeLength=${operation.xmlEnvelope.length}" }

        val response: HttpResponse = try {
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
        } catch (e: HttpRequestTimeoutException) {
            log.error {
                "[SWS] ${operation.name} timed out url=$baseUrl message=${e.message}"
            }

            throw SwsTimeoutException(
                message = "SWS ${operation.name} timed out"
            )
        }

        val responseBody = response.bodyAsText()

        log.info { "[SWS] ${operation.name} response <- status=${response.status.value}" }
        log.debug { "[SWS] ${operation.name} response bodyLength=${responseBody.length}" }

        SwsSoapResponseValidator.validate(
            status = response.status,
            operationName = operation.name
        )

        return SwsSoapResponse(
            body = responseBody,
            headers = response.headers,
            statusCode = response.status.value
        )
    }
}