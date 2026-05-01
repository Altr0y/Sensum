package si.sensum.gm.auth

class StaticGmTokenValidator(
    private val expectedToken: String
) : GmTokenValidator {

    override fun isValid(token: String): Boolean {
        return token == expectedToken
    }
}