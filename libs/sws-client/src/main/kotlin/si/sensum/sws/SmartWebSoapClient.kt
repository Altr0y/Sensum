package si.sensum.sws

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import si.sensum.sws.model.SwsSession

class SmartWebSoapClient(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    suspend fun login(username: String, password: String): SwsSession {
        val xmlBody = SwsXmlBuilder.buildLoginRequest(
            username = username,
            password = password
        )

        val response: HttpResponse = httpClient.post(baseUrl) {
            applySoapHeaders(SwsConstants.LOGIN_ACTION)
            setBody(xmlBody)
        }

        val setCookieHeaders = response.headers.getAll(HttpHeaders.SetCookie).orEmpty()
        val rawSetCookie = setCookieHeaders.firstOrNull()
            ?: error("SWS login succeeded without Set-Cookie header")

        val cookiePair = rawSetCookie.substringBefore(";")
        val separatorIndex = cookiePair.indexOf('=')

        require(separatorIndex > 0) {
            "Invalid Set-Cookie header format: $rawSetCookie"
        }

        val cookieName = cookiePair.substring(0, separatorIndex)
        val cookieValue = cookiePair.substring(separatorIndex + 1)

        return SwsSession(
            cookieName = cookieName,
            cookieValue = cookieValue
        )
    }
}