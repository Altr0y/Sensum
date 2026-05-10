package si.sensum.gm.config

import io.ktor.server.application.*
import si.sensum.sws.SwsConfig

fun Application.loadGmAppConfig(): GmAppConfig {
    val config = environment.config

    val swsBaseUrl = config.property("sws.baseUrl").getString()
    val swsTimeout = config.property("sws.timeoutMillis").getString().toLong()

    val tokenTtlHours = config.property("gm.auth.tokenTtlHours").getString().toLong()
    val gmApiToken = config.property("gm.auth.apiToken").getString()

    require(swsBaseUrl.isNotBlank()) {
        "Missing SWS baseUrl (set SWS_BASE_URL env variable)"
    }

    require(gmApiToken.isNotBlank()) {
        "Missing GM API token (set GM_API_AUTH_TOKEN env variable)"
    }

    return GmAppConfig(
        sws = SwsConfig(
            baseUrl = swsBaseUrl,
            timeoutMillis = swsTimeout
        ),
        auth = GmAuthConfig(
            tokenTtlHours = tokenTtlHours,
            apiToken = gmApiToken
        )
    )
}