package si.sensum.api.backend

import si.sensum.shared.http.ServiceHttpClient
import si.sensum.shared.models.api.AuthenticatedUserResponse
import si.sensum.shared.models.api.LoginRequest

internal class BackendAuthClient(
    private val backend: ServiceHttpClient
) {
    suspend fun verifyLogin(request: LoginRequest): AuthenticatedUserResponse {
        return backend.post("/api/v1/auth/verify", request)
    }
}