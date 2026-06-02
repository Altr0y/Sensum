package si.sensum.gm.model

import java.time.Instant

internal data class GmUserSession(
    val gmToken: String,
    val swsCookieName: String,
    val swsCookieValue: String,
    val username: String,
    val createdAt: Instant,
    val expiresAt: Instant
)