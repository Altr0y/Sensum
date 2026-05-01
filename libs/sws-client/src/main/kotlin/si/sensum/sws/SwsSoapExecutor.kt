package si.sensum.sws

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import si.sensum.logging.Logger
import si.sensum.sws.model.SwsSession
import si.sensum.sws.model.SwsSoapResponse
import si.sensum.sws.model.asCookieHeader

internal class SwsSoapExecutor(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    private val log = Logger.log

    suspend fun execute(
        action: String,
        xmlBody: String,
        session: SwsSession? = null,
        operationName: String
    ): String {
        return executeWithHeaders(
            action = action,
            xmlBody = xmlBody,
            session = session,
            operationName = operationName
        ).body
    }

    suspend fun executeWithHeaders(
        action: String,
        xmlBody: String,
        session: SwsSession? = null,
        operationName: String
    ): SwsSoapResponse {
        log.info { "[SWS] $operationName request -> $baseUrl" }
        log.debug { "[SWS] $operationName request bodyLength=${xmlBody.length}" }

        val response: HttpResponse = httpClient.post(baseUrl) {
            applySoapHeaders(action)

            session?.let {
                header(HttpHeaders.Cookie, it.asCookieHeader())
            }

            setBody(xmlBody)
        }

        val responseBody = response.bodyAsText()

        log.info { "[SWS] $operationName response <- status=${response.status.value}" }
        log.debug { "[SWS] $operationName response bodyLength=${responseBody.length}" }

        SwsSoapResponseValidator.validate(
            status = response.status,
            operationName = operationName
        )

        return SwsSoapResponse(
            body = responseBody,
            headers = response.headers
        )
    }
}