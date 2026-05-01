package si.sensum.gm.auth

interface GmTokenValidator {
    fun isValid(token: String): Boolean
}