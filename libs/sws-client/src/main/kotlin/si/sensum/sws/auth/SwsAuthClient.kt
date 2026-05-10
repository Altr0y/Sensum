package si.sensum.sws.auth

import io.ktor.http.HttpHeaders
import si.sensum.logging.Logger
import si.sensum.sws.SwsSoapExecutor
import si.sensum.sws.SwsUnauthorizedException
import si.sensum.sws.model.SwsSession
import si.sensum.sws.operation.LoginOperation
import si.sensum.sws.operation.LogoutOperation
import si.sensum.sws.parser.extractLoginResult
import si.sensum.sws.parser.extractSessionFromSetCookie

internal class SwsAuthClient(
    private val soapExecutor: SwsSoapExecutor
) {

    private val log = Logger.log

    suspend fun login(
        username: String,
        password: String
    ): SwsSession {
        val operation = LoginOperation.create(
            username = username,
            password = password
        )

        val response = soapExecutor.executeWithHeaders(
            operation = operation
        )

        val loginSucceeded = extractLoginResult(response.body)

        if (!loginSucceeded) {
            log.warn { "[SWS] Login failed: SOAP response indicates invalid credentials" }
            throw SwsUnauthorizedException()
        }

        val session = extractSessionFromSetCookie(
            response.headers.getAll(HttpHeaders.SetCookie).orEmpty()
        )

        log.info { "[SWS] Login success: cookieName=${session.cookieName}" }

        return session
    }

    suspend fun logout(session: SwsSession) {
        val operation = LogoutOperation.create()

        soapExecutor.execute(
            operation = operation,
            session = session
        )
    }
}