package si.sensum.sws.model

data class SwsSession(
    val cookieName: String,
    val cookieValue: String
)

fun SwsSession.asCookieHeader(): String {
    return "$cookieName=$cookieValue"
}