package si.sensum.gm.auth

import si.sensum.shared.auth.bearer.TokenValidator

class StaticGmTokenValidator(
    private val expectedToken: String
) : TokenValidator {

    override fun isValid(token: String): Boolean {
        return token == expectedToken
    }
}