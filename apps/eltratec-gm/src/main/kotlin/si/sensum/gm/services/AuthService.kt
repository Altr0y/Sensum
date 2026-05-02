package si.sensum.gm.services

import si.sensum.logging.Logger
import si.sensum.shared.auth.model.UserSession
import si.sensum.shared.auth.service.TokenService
import si.sensum.shared.auth.store.SessionStore
import si.sensum.shared.models.api.LoginResponse
import si.sensum.sws.SmartWebSoapClient
import java.time.Instant

class AuthService(
    private val soapClient: SmartWebSoapClient,
    private val tokenService: TokenService,
    private val sessionStore: SessionStore
) {

    private val log = Logger.log

    fun findSession(token: String): UserSession? {
        return sessionStore.findByToken(token)
    }

    suspend fun login(username: String, password: String): LoginResponse {
        log.info { "[GM] Login started for user=$username" }

        val swsSession = soapClient.login(username, password)

        log.info { "[GM] SWS login success for user=$username" }

        val now = Instant.now()
        val token = tokenService.generateToken()
        val expiresAt = tokenService.expiry(now)

        val session = UserSession(
            gmToken = token,
            swsCookieName = swsSession.cookieName,
            swsCookieValue = swsSession.cookieValue,
            username = username,
            createdAt = now,
            expiresAt = expiresAt
        )

        sessionStore.save(session)

        log.info { "[GM] Session stored for user=$username" }
        log.debug { "[GM] Token generated for user=$username, expiresAt=$expiresAt" }

        return LoginResponse(
            token = token,
            expiresAt = expiresAt.toString()
        )
    }
}