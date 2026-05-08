package si.sensum.sws.parser

import si.sensum.sws.SwsInvalidResponseException
import si.sensum.sws.model.SwsSession

internal fun extractSessionFromSetCookie(setCookieHeaders: List<String>): SwsSession {
    val rawSetCookie = setCookieHeaders.firstOrNull { it.contains("=") }
        ?: throw SwsInvalidResponseException(
            "SWS login succeeded but no Set-Cookie header was returned"
        )

    val cookiePair = rawSetCookie.substringBefore(";").trim()
    val separatorIndex = cookiePair.indexOf('=')

    if (separatorIndex <= 0) {
        throw SwsInvalidResponseException(
            "Invalid Set-Cookie header format returned by SWS"
        )
    }

    val cookieName = cookiePair.substring(0, separatorIndex).trim()
    val cookieValue = cookiePair.substring(separatorIndex + 1).trim()

    if (cookieName.isBlank() || cookieValue.isBlank()) {
        throw SwsInvalidResponseException(
            "SWS returned empty cookie name or value"
        )
    }

    return SwsSession(
        cookieName = cookieName,
        cookieValue = cookieValue
    )
}