package si.sensum.shared.auth.bearer

interface TokenValidator {
    fun isValid(token: String): Boolean
}