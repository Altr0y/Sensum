package si.sensum.sws

import si.sensum.sws.model.SwsSession

interface SmartWebSoapClient {
    suspend fun login(username: String, password: String): SwsSession
}