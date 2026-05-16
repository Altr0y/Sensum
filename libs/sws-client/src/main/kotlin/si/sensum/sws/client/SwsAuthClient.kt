package si.sensum.sws.client

import io.ktor.http.HttpHeaders
import si.sensum.logging.Logger
import si.sensum.sws.SwsSoapExecutor
import si.sensum.sws.SwsUnauthorizedException
import si.sensum.sws.model.SwsSession
import si.sensum.sws.model.SwsSoapResponse
import si.sensum.sws.operation.SwsOperations
import si.sensum.sws.parser.SwsCookieParser
import si.sensum.sws.parser.SwsLoginResponseParser

internal class SwsAuthClient(
    private val soapExecutor: SwsSoapExecutor
) {
    private val log = Logger.log

    suspend fun login(
        username: String,
        password: String
    ): SwsSession {
        val response = executeLogin(
            username = username,
            password = password
        )

        validateLoginResponse(response)

        val session = extractSession(response)

        log.info { "[SWS] Login success: cookieName=${session.cookieName}" }

        return session
    }

    suspend fun logout(session: SwsSession) {
        soapExecutor.execute(
            operation = SwsOperations.simple("Logout"),
            session = session
        )
    }

    private suspend fun executeLogin(
        username: String,
        password: String
    ): SwsSoapResponse {
        return soapExecutor.executeWithHeaders(
            operation = SwsOperations.simple(
                methodName = "Login",
                parameters = listOf(
                    "UserName" to username,
                    "Password" to password
                )
            )
        )
    }

    private fun validateLoginResponse(response: SwsSoapResponse) {
        val loginSucceeded = SwsLoginResponseParser.extractLoginResult(response.body)

        if (!loginSucceeded) {
            log.warn { "[SWS] Login failed: SOAP response indicates invalid credentials" }
            throw SwsUnauthorizedException(
                message = "Invalid SWS username or password"
            )
        }
    }

    private fun extractSession(response: SwsSoapResponse): SwsSession {
        return SwsCookieParser.extractSessionFromSetCookie(
            response.headers.getAll(HttpHeaders.SetCookie).orEmpty()
        )
    }
}