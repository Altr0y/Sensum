package si.sensum.gm.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import si.sensum.gm.config.GmAppConfig
import si.sensum.shared.auth.jwt.JwtConfig
import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.models.common.ApiError

private const val SERVICE_JWT_AUTH_NAME = "service-jwt"
private const val ALLOWED_SERVICE_NAME = "backend-core"

internal fun Application.installGmServiceJwtAuthentication(config: GmAppConfig) {
    val serviceJwtTokenService = JwtTokenService(
        config = JwtConfig(
            secret = config.auth.serviceJwtSecret,
            issuer = config.auth.serviceJwtIssuer,
            audience = config.auth.serviceJwtAudience,
            ttlSeconds = 3600
        )
    )

    install(Authentication) {
        jwt(SERVICE_JWT_AUTH_NAME) {
            realm = config.auth.serviceJwtRealm

            verifier(serviceJwtTokenService.verifier())

            validate { credential ->
                val serviceName = credential.payload
                    .getClaim("service")
                    .asString()

                if (serviceName == ALLOWED_SERVICE_NAME) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiError(error = "Invalid or expired service JWT")
                )
            }
        }
    }
}

internal object GmAuthNames {
    const val SERVICE_JWT = SERVICE_JWT_AUTH_NAME
}