package si.sensum.api.auth

import si.sensum.shared.auth.bearer.TokenValidator

class StaticApiTokenValidator(
    private val expectedToken: String
) : TokenValidator {

    override fun isValid(token: String): Boolean {
        return token == expectedToken
    }
}