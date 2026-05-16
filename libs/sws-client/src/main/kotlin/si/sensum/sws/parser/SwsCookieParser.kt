package si.sensum.sws.parser

import si.sensum.sws.SwsInvalidResponseException
import si.sensum.sws.model.SwsSession

internal object SwsCookieParser {

    fun extractSessionFromSetCookie(
        setCookieHeaders: List<String>
    ): SwsSession {
        val cookiePair = findCookiePair(setCookieHeaders)
        val cookie = parseCookiePair(cookiePair)

        return SwsSession(
            cookieName = cookie.name,
            cookieValue = cookie.value
        )
    }

    private fun findCookiePair(
        setCookieHeaders: List<String>
    ): String {
        return setCookieHeaders
            .asSequence()
            .map { header -> header.substringBefore(";").trim() }
            .firstOrNull { cookiePair -> cookiePair.isValidCookiePairCandidate() }
            ?: throw SwsInvalidResponseException(
                "SWS login succeeded but no valid Set-Cookie header was returned"
            )
    }

    private fun String.isValidCookiePairCandidate(): Boolean {
        val separatorIndex = indexOf('=')

        return isNotBlank() &&
                separatorIndex > 0 &&
                separatorIndex < lastIndex
    }

    private fun parseCookiePair(cookiePair: String): SwsCookie {
        val separatorIndex = cookiePair.indexOf('=')

        val cookie = SwsCookie(
            name = cookiePair.substring(0, separatorIndex).trim(),
            value = cookiePair.substring(separatorIndex + 1).trim()
        )

        validateCookie(cookie)

        return cookie
    }

    private fun validateCookie(cookie: SwsCookie) {
        if (cookie.name.isBlank() || cookie.value.isBlank()) {
            throw SwsInvalidResponseException(
                "SWS returned empty cookie name or value"
            )
        }
    }

    private data class SwsCookie(
        val name: String,
        val value: String
    )
}