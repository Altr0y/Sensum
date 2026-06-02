package si.sensum.sws.parser

import si.sensum.sws.SwsInvalidResponseException

internal object SwsLoginResponseParser {

    fun extractLoginResult(responseBody: String): Boolean {
        val document = SwsXmlDocumentParser.parse(
            xml = responseBody,
            responseName = "login"
        )

        val value = document
            .elementsByTagName("LoginResult")
            .firstOrNull()
            ?.textContent
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: throw SwsInvalidResponseException(
                "SWS login response missing <LoginResult>"
            )

        return parseLoginResult(value)
    }

    private fun parseLoginResult(value: String): Boolean {
        return when (value.lowercase()) {
            "true" -> true
            "false" -> false
            else -> throw SwsInvalidResponseException(
                "Invalid LoginResult value: $value"
            )
        }
    }
}