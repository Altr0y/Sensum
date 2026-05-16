package si.sensum.shared.auth.store

interface SessionStore<T : Any> {
    fun save(
        token: String,
        session: T
    )

    fun findByToken(token: String): T?

    fun deleteByToken(token: String)
}