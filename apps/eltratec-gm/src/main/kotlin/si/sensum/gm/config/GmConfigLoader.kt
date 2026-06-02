package si.sensum.gm.config

import io.ktor.server.application.Application
import si.sensum.sws.SwsConfig

internal fun Application.loadGmAppConfig(): GmAppConfig {
    val config = environment.config

    val swsBaseUrl = config.property("sws.baseUrl").getString()
    val swsTimeout = config.property("sws.timeoutMillis").getString().toLong()

    val tokenTtlHours = config.property("gm.auth.tokenTtlHours").getString().toLong()

    val serviceJwtSecret = config.property("gm.serviceJwt.secret").getString()
    val serviceJwtIssuer = config.property("gm.serviceJwt.issuer").getString()
    val serviceJwtAudience = config.property("gm.serviceJwt.audience").getString()
    val serviceJwtRealm = config.property("gm.serviceJwt.realm").getString()

    require(swsBaseUrl.isNotBlank()) {
        "Missing SWS baseUrl (set SWS_BASE_URL env variable)"
    }

    require(serviceJwtSecret.length >= 32) {
        "GM service JWT secret must be at least 32 characters long."
    }

    return GmAppConfig(
        sws = SwsConfig(
            baseUrl = swsBaseUrl,
            timeoutMillis = swsTimeout
        ),
        auth = GmAuthConfig(
            tokenTtlHours = tokenTtlHours,
            serviceJwtSecret = serviceJwtSecret,
            serviceJwtIssuer = serviceJwtIssuer,
            serviceJwtAudience = serviceJwtAudience,
            serviceJwtRealm = serviceJwtRealm
        )
    )
}