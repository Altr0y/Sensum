package si.sensum.api.di

import si.sensum.api.backend.BackendChannelClient
import si.sensum.api.backend.BackendMeasurementClient
import si.sensum.api.backend.BackendStationClient
import si.sensum.api.services.AuthService
import si.sensum.shared.auth.jwt.JwtTokenService

internal data class ApiDependencies(
    val jwtTokenService: JwtTokenService,
    val authService: AuthService,
    val backendMeasurements: BackendMeasurementClient,
    val backendStations: BackendStationClient,
    val backendChannels: BackendChannelClient
)