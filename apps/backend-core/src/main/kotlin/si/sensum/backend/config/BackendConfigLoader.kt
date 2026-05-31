package si.sensum.backend.config

import io.ktor.server.application.Application

fun Application.loadBackendConfig(): BackendConfig {
    val config = environment.config

    return BackendConfig(
        gmBaseUrl = config.property("gm.baseUrl").getString(),
        gmServiceJwtSecret = config.property("gm.serviceJwt.secret").getString(),
        gmServiceJwtIssuer = config.property("gm.serviceJwt.issuer").getString(),
        gmServiceJwtAudience = config.property("gm.serviceJwt.audience").getString(),
        gmServiceJwtTtlSeconds = config.property("gm.serviceJwt.ttlSeconds").getString().toLong(),
        swsUsername = config.propertyOrNull("sws.username")?.getString().orEmpty(),
        swsPassword = config.propertyOrNull("sws.password")?.getString().orEmpty()
    )
}