package si.sensum.sws

import io.ktor.client.*
import io.ktor.http.HttpHeaders
import si.sensum.logging.Logger
import si.sensum.sws.model.SwsSession
import si.sensum.sws.parser.extractLoginResult
import si.sensum.sws.parser.extractSessionFromSetCookie
import si.sensum.sws.xml.SwsXmlBuilder

class SmartWebSoapClient(
    httpClient: HttpClient,
    baseUrl: String
) {

    private val log = Logger.log

    private val soapExecutor = SwsSoapExecutor(
        httpClient = httpClient,
        baseUrl = baseUrl
    )

    suspend fun login(username: String, password: String): SwsSession {
        val xmlBody = SwsXmlBuilder.buildLoginRequest(
            username = username,
            password = password
        )

        val response = soapExecutor.executeWithHeaders(
            action = SwsConstants.LOGIN_ACTION,
            xmlBody = xmlBody,
            operationName = "Login"
        )

        val loginSucceeded = extractLoginResult(response.body)

        if (!loginSucceeded) {
            log.warn { "[SWS] Login failed: SOAP response indicates invalid credentials" }
            throw SwsUnauthorizedException()
        }

        val session = extractSessionFromSetCookie(
            response.headers.getAll(HttpHeaders.SetCookie).orEmpty()
        )

        log.info { "[SWS] Login success: cookieName=${session.cookieName}" }

        return session
    }

    suspend fun getAllMeasurementsRaw(
        session: SwsSession,
        datetimeFrom: String,
        datetimeTo: String
    ): String {
        val xmlBody = SwsXmlBuilder.buildGetAllMeasurementsRequest(
            datetimeFrom = datetimeFrom,
            datetimeTo = datetimeTo
        )

        return soapExecutor.execute(
            action = SwsConstants.GET_ALL_MEASUREMENTS_ACTION,
            xmlBody = xmlBody,
            session = session,
            operationName = "GetAllMeasurements"
        )
    }
}