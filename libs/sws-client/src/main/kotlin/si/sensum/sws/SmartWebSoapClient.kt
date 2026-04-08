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

        println("=== SWS LOGIN REQUEST ===")
        println("POST $baseUrl")
        println("SOAPAction: \"${SwsConstants.LOGIN_ACTION}\"")
        println("Content-Type: text/xml; charset=utf-8")
        println(xmlBody)

        val response: HttpResponse = httpClient.post(baseUrl) {
            applySoapHeaders(SwsConstants.LOGIN_ACTION)
            setBody(xmlBody)
        }

        println("=== REQUEST HEADERS ===")
        println("Content-Type: text/xml; charset=utf-8")
        println("SOAPAction: \"${SwsConstants.LOGIN_ACTION}\"")

        val responseBody = response.bodyAsText()
        val setCookieHeaders = response.headers.getAll(HttpHeaders.SetCookie).orEmpty()

        println("=== RESPONSE HEADERS ===")
        response.headers.entries().forEach { (name, values) ->
            println("$name: ${values.joinToString()}")
        }

        println("=== SWS LOGIN RESPONSE ===")
        println("HTTP status: ${response.status}")
        println("Set-Cookie headers: $setCookieHeaders")
        println(responseBody)

        if (!response.status.isSuccess()) {
            error(
                "SWS login HTTP call failed with status ${response.status.value}. " +
                        "Response body: $responseBody"
            )
        }

        val rawSetCookie = setCookieHeaders.firstOrNull { it.contains("=") }
            ?: error(
                "SWS login returned no Set-Cookie header. " +
                        "Status: ${response.status.value}, Response body: $responseBody"
            )


        val cookiePair = rawSetCookie.substringBefore(";").trim()
        val separatorIndex = cookiePair.indexOf('=')

        require(separatorIndex > 0) {
            "Invalid Set-Cookie header format: $rawSetCookie"
        }

        val cookieName = cookiePair.substring(0, separatorIndex).trim()
        val cookieValue = cookiePair.substring(separatorIndex + 1).trim()

        return SwsSession(
            cookieName = cookieName,
            cookieValue = cookieValue
        )
    }
}