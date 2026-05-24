package si.sensum.api.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import si.sensum.api.config.ApiGatewayConfig
import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.models.api.ApiErrorResponse

internal fun Application.installApiJwtAuthentication(
    config: ApiGatewayConfig,
    jwtTokenService: JwtTokenService
) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = config.jwtRealm

            verifier(jwtTokenService.verifier())

            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                val username = credential.payload.getClaim("username").asString()
                val customerId = credential.payload.getClaim("customerId").asInt()
                val role = credential.payload.getClaim("role").asString()

                if (
                    userId != null &&
                    !username.isNullOrBlank() &&
                    customerId != null &&
                    !role.isNullOrBlank()
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiErrorResponse(
                        error = "Token is not valid or has expired"
                    )
                )
            }
        }
    }
}