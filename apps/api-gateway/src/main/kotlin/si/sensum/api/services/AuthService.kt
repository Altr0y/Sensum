package si.sensum.api.services

import si.sensum.api.backend.BackendAuthClient
import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.auth.jwt.JwtUser
import si.sensum.shared.models.auth.LoginCommand
import si.sensum.shared.models.auth.LoginResult

internal class AuthService(
    private val backendAuthClient: BackendAuthClient,
    private val jwtTokenService: JwtTokenService
) {
    suspend fun login(request: LoginCommand): LoginResult {
        val user = backendAuthClient.verifyLogin(request)

        val token = jwtTokenService.generateToken(
            JwtUser(
                userId = user.id,
                username = user.username,
                customerId = user.customerId,
                role = user.role
            )
        )

        return LoginResult(
            token = token,
            expiresAt = jwtTokenService.expiresAt().toString()
        )
    }

    fun logout(): Map<String, String> {
        // JWT je stateless. Pravi logout za zdaj pomeni:
        // desktop/Next.js token odstrani na client strani.
        return mapOf("status" to "logged_out")
    }
}