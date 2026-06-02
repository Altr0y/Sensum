package si.sensum.shared.auth.store

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class InMemorySessionStore<T : Any>(
    private val expiresAt: (T) -> Instant
) : SessionStore<T> {

    private val sessions = ConcurrentHashMap<String, T>()

    override fun save(
        token: String,
        session: T
    ) {
        sessions[token] = session
    }

    override fun findByToken(token: String): T? {
        val session = sessions[token] ?: return null

        return if (expiresAt(session).isAfter(Instant.now())) {
            session
        } else {
            sessions.remove(token)
            null
        }
    }

    override fun deleteByToken(token: String) {
        sessions.remove(token)
    }
}