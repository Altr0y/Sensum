package si.sensum.shared.auth.model

import java.time.Instant

data class UserSession(
    val gmToken: String,
    val swsCookieName: String,
    val swsCookieValue: String,
    val username: String,
    val createdAt: Instant,
    val expiresAt: Instant
)