package si.sensum.shared.auth.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateCrtKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.time.Instant
import java.util.Base64
import java.util.Date

class JwtTokenService(
    private val config: JwtConfig
) {
    private val keyPair: KeyPair? = when {
        config.privateKeyBase64 == null -> null
        config.privateKeyBase64.isBlank() -> generateKeyPair()
        else -> loadKeyPair(config.privateKeyBase64)
    }

    private val algorithm: Algorithm = when (val kp = keyPair) {
        null -> Algorithm.HMAC256(config.secret)
        else -> Algorithm.RSA256(kp.public as RSAPublicKey, kp.private as RSAPrivateKey)
    }

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

        user.userId?.let { builder.withClaim("userId", it) }
        user.customerId?.let { builder.withClaim("customerId", it) }
        user.serviceName?.let { builder.withClaim("service", it) }

        return builder.sign(algorithm)
    }

    fun expiresAt(now: Instant = Instant.now()): Instant = now.plusSeconds(config.ttlSeconds)

    fun verifier(): JWTVerifier {
        return JWT.require(algorithm)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()
    }

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
                userId = decoded.getClaim("userId").asInt(),
                username = decoded.getClaim("username").asString() ?: decoded.subject,
                customerId = decoded.getClaim("customerId").asInt(),
                role = decoded.getClaim("role").asString() ?: "USER",
                serviceName = decoded.getClaim("service").asString()
            )
        } catch (_: Exception) {
            null
        }
    }

    fun jwks(): Map<String, Any>? {
        val pub = keyPair?.public as? RSAPublicKey ?: return null
        val encoder = Base64.getUrlEncoder().withoutPadding()
        return mapOf(
            "keys" to listOf(
                mapOf(
                    "kty" to "RSA",
                    "use" to "sig",
                    "alg" to "RS256",
                    "kid" to "sensum-1",
                    "n" to encoder.encodeToString(pub.modulus.toByteArray().stripLeadingZero()),
                    "e" to encoder.encodeToString(pub.publicExponent.toByteArray().stripLeadingZero())
                )
            )
        )
    }

    fun jwksJson(): String? {
        val pub = keyPair?.public as? RSAPublicKey ?: return null
        val encoder = Base64.getUrlEncoder().withoutPadding()
        val n = encoder.encodeToString(pub.modulus.toByteArray().stripLeadingZero())
        val e = encoder.encodeToString(pub.publicExponent.toByteArray().stripLeadingZero())
        return """{"keys":[{"kty":"RSA","use":"sig","alg":"RS256","kid":"sensum-1","n":"$n","e":"$e"}]}"""
    }

    private fun ByteArray.stripLeadingZero(): ByteArray =
        if (isNotEmpty() && this[0] == 0.toByte()) copyOfRange(1, size) else this

    private companion object {
        fun generateKeyPair(): KeyPair =
            KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()

        fun loadKeyPair(base64Der: String): KeyPair {
            val keyBytes = Base64.getDecoder().decode(base64Der)
            val privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(PKCS8EncodedKeySpec(keyBytes)) as RSAPrivateCrtKey
            val publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(RSAPublicKeySpec(privateKey.modulus, privateKey.publicExponent)) as RSAPublicKey
            return KeyPair(publicKey, privateKey)
        }
    }
}
