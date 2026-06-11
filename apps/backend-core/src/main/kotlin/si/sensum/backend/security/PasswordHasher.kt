package si.sensum.backend.security

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    private const val COST = 12

    private val BCRYPT_PREFIX_REGEX =
        Regex("""^\$2[aby]\$(0[4-9]|[12][0-9]|3[01])\$""")

    fun hash(rawPassword: String): String {
        require(rawPassword.isNotBlank()) {
            "Password must not be blank"
        }

        return BCrypt.withDefaults()
            .hashToString(COST, rawPassword.toCharArray())
    }

    fun verify(
        rawPassword: String,
        storedPasswordHash: String
    ): Boolean {
        if (rawPassword.isBlank()) {
            return false
        }

        val normalizedHash = storedPasswordHash.trim()

        if (!isBcryptHash(normalizedHash)) {
            return false
        }

        return BCrypt.verifyer()
            .verify(rawPassword.toCharArray(), normalizedHash)
            .verified
    }

    fun isBcryptHash(value: String): Boolean {
        return BCRYPT_PREFIX_REGEX.containsMatchIn(value.trim())
    }
}