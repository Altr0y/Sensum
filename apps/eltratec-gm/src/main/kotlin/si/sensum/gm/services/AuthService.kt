package si.sensum.gm.services

import si.sensum.shared.auth.model.UserSession
import si.sensum.shared.auth.service.TokenService
import si.sensum.shared.auth.store.SessionStore
import si.sensum.shared.models.api.LoginResponse
import si.sensum.sws.SmartWebSoapClient
import si.sensum.sws.model.SwsSession
import java.time.Instant

class AuthService(
    private val soapClient: SmartWebSoapClient,
    private val tokenService: TokenService,
    private val sessionStore: SessionStore
) : GmService("GM AuthService") {

    fun findSession(token: String): UserSession? {
        return sessionStore.findByToken(token)
    }

    suspend fun login(username: String, password: String): LoginResponse {
        return logged(
            operation = "login",
            details = "user=$username"
        ) {
            val swsSession = soapClient.login(username, password)

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

            LoginResponse(
                token = token,
                expiresAt = expiresAt.toString()
            )
        }
    }

    suspend fun logout(token: String) {
        return logged(
            operation = "logout"
        ) {
            val userSession = sessionStore.findByToken(token)
                ?: return@logged

            val swsSession = SwsSession(
                cookieName = userSession.swsCookieName,
                cookieValue = userSession.swsCookieValue
            )

            soapClient.logout(swsSession)

            sessionStore.deleteByToken(token)
        }
    }
}