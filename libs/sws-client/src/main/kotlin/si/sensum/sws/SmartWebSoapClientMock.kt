package si.sensum.sws

import si.sensum.sws.model.SwsLoginResult

class SmartWebSoapClientMock : SmartWebSoapClient {
    override suspend fun login(username: String, password: String): SwsLoginResult {
        return SwsLoginResult(
            sessionId = "mock-session-$username"
        )
    }
}