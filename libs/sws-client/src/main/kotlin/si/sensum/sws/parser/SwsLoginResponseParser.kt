package si.sensum.sws.parser

import si.sensum.sws.SwsInvalidResponseException

internal fun extractLoginResult(responseBody: String): Boolean {
    val regex = Regex(
        "<LoginResult>(.*?)</LoginResult>",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )

    val match = regex.find(responseBody)
        ?: throw SwsInvalidResponseException(
            "SWS login response missing <LoginResult>"
        )

    return when (match.groupValues[1].trim().lowercase()) {
        "true" -> true
        "false" -> false
        else -> throw SwsInvalidResponseException(
            "Invalid LoginResult value: ${match.groupValues[1].trim()}"
        )
    }
}