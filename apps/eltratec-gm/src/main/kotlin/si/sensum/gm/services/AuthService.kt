package si.sensum.gm.services

import si.sensum.gm.auth.toSwsSession
import si.sensum.gm.model.GmUserSession
import si.sensum.shared.auth.service.TokenService
import si.sensum.shared.auth.store.SessionStore
import si.sensum.shared.models.api.LoginResponse
import si.sensum.sws.client.SmartWebSoapClient
import si.sensum.sws.model.SwsSession
import java.time.Instant

internal class AuthService(
    private val soapClient: SmartWebSoapClient,
    private val tokenService: TokenService,
    private val sessionStore: SessionStore<GmUserSession>
) : GmService("GM AuthService") {

    fun findSession(token: String): GmUserSession? {
        return sessionStore.findByToken(token)
    }

    suspend fun login(
        username: String,
        password: String
    ): LoginResponse {
        return logged(
            operation = "login",
            details = "username=$username"
        ) {
            val swsSession = soapClient.login(
                username = username,
                password = password
            )

            val userSession = createUserSession(
                username = username,
                swsSession = swsSession
            )

            sessionStore.save(
                token = userSession.gmToken,
                session = userSession
            )

            userSession.toLoginResponse()
        }
    }

    suspend fun logout(token: String) {
        logged(
            operation = "logout"
        ) {
            val userSession = sessionStore.findByToken(token)
                ?: return@logged

            try {
                soapClient.logout(userSession.toSwsSession())
            } finally {
                sessionStore.deleteByToken(token)
            }
        }
    }

    private fun createUserSession(
        username: String,
        swsSession: SwsSession
    ): GmUserSession {
        val now = Instant.now()
        val token = tokenService.generateToken()
        val expiresAt = tokenService.expiry(now)

        return GmUserSession(
            gmToken = token,
            swsCookieName = swsSession.cookieName,
            swsCookieValue = swsSession.cookieValue,
            username = username,
            createdAt = now,
            expiresAt = expiresAt
        )
    }

    private fun GmUserSession.toLoginResponse(): LoginResponse {
        return LoginResponse(
            token = gmToken,
            expiresAt = expiresAt.toString()
        )
    }
}