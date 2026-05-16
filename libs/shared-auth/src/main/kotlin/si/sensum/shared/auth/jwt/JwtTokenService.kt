package si.sensum.shared.auth.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import java.util.Date

class JwtTokenService(
    private val config: JwtConfig
) {
    private val algorithm = Algorithm.HMAC256(config.secret)

    fun generateToken(user: JwtUser, now: Instant = Instant.now()): String {
        val expiresAt = now.plusSeconds(config.ttlSeconds)

        val builder = JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(user.username)
            .withClaim("username", user.username)
            .withClaim("role", user.role)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expiresAt))

        user.serviceName?.let { serviceName ->
            builder.withClaim("service", serviceName)
        }

        return builder.sign(algorithm)
    }

    fun expiresAt(now: Instant = Instant.now()): Instant {
        return now.plusSeconds(config.ttlSeconds)
    }

    fun verifier() =
        JWT.require(algorithm)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()

    fun isValid(token: String): Boolean {
        return try {
            verifier().verify(token)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun parseUser(token: String): JwtUser? {
        return try {
            val decoded = verifier().verify(token)

            JwtUser(
                username = decoded.getClaim("username").asString() ?: decoded.subject,
                role = decoded.getClaim("role").asString() ?: "USER",
                serviceName = decoded.getClaim("service").asString()
            )
        } catch (_: Exception) {
            null
        }
    }
}