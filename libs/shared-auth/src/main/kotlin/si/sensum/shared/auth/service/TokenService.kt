package si.sensum.shared.auth.service

import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.Base64

class TokenService(
    private val ttl: Duration
) {
    private val secureRandom = SecureRandom()

    fun generateToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes)
    }

    fun expiry(now: Instant = Instant.now()): Instant {
        return now.plus(ttl)
    }
}