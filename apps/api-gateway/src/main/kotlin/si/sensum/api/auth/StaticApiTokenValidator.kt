package si.sensum.api.auth

class StaticApiTokenValidator(
    private val expectedToken: String
) : ApiTokenValidator {

    override fun isValid(token: String): Boolean {
        return token == expectedToken
    }
}