package si.sensum.api.client

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.auth.AuthenticatedUserDto
import si.sensum.shared.models.auth.LoginCommand

internal class BackendAuthClient(
    private val backend: ServiceHttpClient
) {
    suspend fun verifyLogin(request: LoginCommand): AuthenticatedUserDto {
        return backend.post("/api/v1/auth/verify", request)
    }
}