package si.sensum.sws.model

data class SwsSession(
    val cookieName: String,
    val cookieValue: String
)

internal fun SwsSession.asCookieHeader(): String {
    return "$cookieName=$cookieValue"
}