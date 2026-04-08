package si.sensum.sws

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import si.sensum.sws.model.SwsSession
import si.sensum.sws.parser.extractLoginResult
import si.sensum.sws.parser.extractSessionFromSetCookie
import si.sensum.sws.xml.SwsXmlBuilder

class SmartWebSoapClient(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    suspend fun login(username: String, password: String): SwsSession {
        val xmlBody = SwsXmlBuilder.buildLoginRequest(
            username = username,
            password = password
        )

        println("[SWS] Login request -> $baseUrl")

        val response: HttpResponse = httpClient.post(baseUrl) {
            applySoapHeaders(SwsConstants.LOGIN_ACTION)
            setBody(xmlBody)
        }

        val responseBody = response.bodyAsText()
        val setCookieHeaders = response.headers.getAll(HttpHeaders.SetCookie).orEmpty()

        println("[SWS] Login response <- status=${response.status.value}")

        if (response.status == HttpStatusCode.Unauthorized) {
            println("[SWS] Login failed: invalid credentials")
            throw SwsUnauthorizedException()
        }

        if (!response.status.isSuccess()) {
            println("[SWS] Login failed: HTTP ${response.status.value}")
            throw SwsHttpException(
                statusCode = response.status.value,
                message = "SWS login failed with HTTP ${response.status.value}"
            )
        }

        val loginSucceeded = extractLoginResult(responseBody)
        if (!loginSucceeded) {
            println("[SWS] Login failed: invalid credentials")
            throw SwsUnauthorizedException()
        }

        val session = extractSessionFromSetCookie(setCookieHeaders)

        println("[SWS] Login success: cookie=${session.cookieName}")

        return session
    }
}