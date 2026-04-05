package si.sensum.sws

import si.sensum.sws.model.SwsSession

class SmartWebSoapClientMock : SmartWebSoapClient {
    override suspend fun login(username: String, password: String): SwsSession {
        return SwsSession(
            cookieName = "ASP.NET_SessionId",
            cookieValue = "mock-session-$username"
        )
    }
}