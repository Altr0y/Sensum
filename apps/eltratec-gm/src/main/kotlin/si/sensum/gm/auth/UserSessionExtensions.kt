package si.sensum.gm.auth

import si.sensum.gm.model.GmUserSession
import si.sensum.sws.model.SwsSession

internal fun GmUserSession.toSwsSession(): SwsSession {
    return SwsSession(
        cookieName = swsCookieName,
        cookieValue = swsCookieValue
    )
}