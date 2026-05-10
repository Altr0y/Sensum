package si.sensum.shared.auth.store

import si.sensum.shared.auth.model.UserSession

interface SessionStore {
    fun save(session: UserSession)
    fun findByToken(token: String): UserSession?
    fun deleteByToken(token: String)
}