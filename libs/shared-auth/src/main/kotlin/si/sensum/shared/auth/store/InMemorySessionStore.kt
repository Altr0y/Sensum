package si.sensum.shared.auth.store

import si.sensum.shared.auth.model.UserSession
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class InMemorySessionStore : SessionStore {

    private val sessions = ConcurrentHashMap<String, UserSession>()

    override fun save(session: UserSession) {
        sessions[session.gmToken] = session
    }

    override fun findByToken(token: String): UserSession? {
        val session = sessions[token] ?: return null

        return if (session.expiresAt.isAfter(Instant.now())) {
            session
        } else {
            sessions.remove(token)
            null
        }
    }
}