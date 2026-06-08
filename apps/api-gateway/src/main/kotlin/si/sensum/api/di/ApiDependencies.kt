package si.sensum.api.di

import si.sensum.api.service.AuthService
import si.sensum.api.service.ChannelService
import si.sensum.api.service.CustomerService
import si.sensum.api.service.DslService
import si.sensum.api.service.MeasurementService
import si.sensum.api.service.RecordsService
import si.sensum.api.service.StationService
import si.sensum.api.service.StatsService
import si.sensum.api.service.UserService
import si.sensum.shared.auth.jwt.JwtTokenService

internal data class ApiDependencies(
    val jwtTokenService: JwtTokenService,
    val authService: AuthService,
    val customerService: CustomerService,
    val userService: UserService,
    val stationService: StationService,
    val channelService: ChannelService,
    val measurementService: MeasurementService,
    val recordsService: RecordsService,
    val dslService: DslService,
    val statsService: StatsService
)