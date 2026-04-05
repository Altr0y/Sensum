package si.sensum.sws

import si.sensum.sws.model.SwsLoginResult

interface SmartWebSoapClient {
    suspend fun login(username: String, password: String): SwsLoginResult
}