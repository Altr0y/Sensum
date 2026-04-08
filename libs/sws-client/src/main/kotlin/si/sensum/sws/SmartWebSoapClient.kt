package si.sensum.sws

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import si.sensum.sws.model.SwsSession
import si.sensum.sws.parser.extractLoginResult
import si.sensum.sws.parser.extractSessionFromSetCookie
import si.sensum.sws.xml.SwsXmlBuilder
import si.sensum.logging.Logger

class SmartWebSoapClient(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    private val log = Logger.log

    suspend fun login(username: String, password: String): SwsSession {
        val xmlBody = SwsXmlBuilder.buildLoginRequest(
            username = username,
            password = password
        )

        // println("[SWS] Login request -> $baseUrl")
        log.info { "[SWS] Login request -> $baseUrl" }
        log.debug { "[SWS] Login request bodyLength=${xmlBody.length}" }

        val response: HttpResponse = httpClient.post(baseUrl) {
            applySoapHeaders(SwsConstants.LOGIN_ACTION)
            setBody(xmlBody)
        }

        val responseBody = response.bodyAsText()
        val setCookieHeaders = response.headers.getAll(HttpHeaders.SetCookie).orEmpty()

        // println("[SWS] Login response <- status=${response.status.value}")
        log.info { "[SWS] Login response <- status=${response.status.value}" }
        log.debug {
            "[SWS] Login response bodyLength=${responseBody.length}, setCookieCount=${setCookieHeaders.size}"
        }

        if (response.status == HttpStatusCode.Unauthorized) {
            // println("[SWS] Login failed: invalid credentials")
            log.warn { "[SWS] Login failed: invalid credentials (HTTP 401)" }
            throw SwsUnauthorizedException()
        }

        if (!response.status.isSuccess()) {
            // println("[SWS] Login failed: HTTP ${response.status.value}")
            log.error { "[SWS] Login failed: HTTP ${response.status.value}" }
            throw SwsHttpException(
                statusCode = response.status.value,
                message = "SWS login failed with HTTP ${response.status.value}"
            )
        }

        val loginSucceeded = extractLoginResult(responseBody)

        if (!loginSucceeded) {
            // println("[SWS] Login failed: invalid credentials")
            log.warn { "[SWS] Login failed: SOAP response indicates invalid credentials" }
            throw SwsUnauthorizedException()
        }

        val session = extractSessionFromSetCookie(setCookieHeaders)

        // println("[SWS] Login success: cookie=${session.cookieName}")
        log.info { "[SWS] Login success: cookieName=${session.cookieName}" }

        return session
    }
}