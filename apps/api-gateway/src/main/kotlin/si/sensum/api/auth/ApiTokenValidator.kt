package si.sensum.api.auth

interface ApiTokenValidator {
    fun isValid(token: String): Boolean
}