package si.sensum.api.di

import si.sensum.api.client.BackendAuthClient
import si.sensum.api.client.BackendChannelClient
import si.sensum.api.client.BackendCustomerClient
import si.sensum.api.client.BackendDataImportClient
import si.sensum.api.client.BackendDslClient
import si.sensum.api.client.BackendMeasurementClient
import si.sensum.api.client.BackendRecordsClient
import si.sensum.api.client.BackendSimulatorClient
import si.sensum.api.client.BackendStatsClient
import si.sensum.api.client.BackendStationClient
import si.sensum.api.client.BackendUserClient
import si.sensum.api.config.ApiGatewayConfig
import si.sensum.api.config.createHttpClient
import si.sensum.api.service.AuthService
import si.sensum.api.service.ChannelService
import si.sensum.api.service.CustomerService
import si.sensum.api.service.DataImportService
import si.sensum.api.service.DslService
import si.sensum.api.service.MeasurementService
import si.sensum.api.service.RecordsService
import si.sensum.api.service.SimulatorService
import si.sensum.api.service.StationService
import si.sensum.api.service.StatsService
import si.sensum.api.service.UserService
import si.sensum.shared.auth.jwt.JwtConfig
import si.sensum.shared.auth.jwt.JwtTokenService
import si.sensum.shared.http.ServiceHttpClient

internal fun createApiDependencies(
    config: ApiGatewayConfig
): ApiDependencies {
    val jwtTokenService = JwtTokenService(
        config = JwtConfig(
            privateKeyBase64 = config.jwtPrivateKeyBase64,
            issuer = config.jwtIssuer,
            audience = config.jwtAudience,
            ttlSeconds = config.jwtTtlSeconds
        )
    )

    val httpClient = createHttpClient()

    val backendHttpClient = ServiceHttpClient(
        httpClient = httpClient,
        baseUrl = config.backendCoreBaseUrl
    )

    val authClient = BackendAuthClient(backendHttpClient)
    val customerClient = BackendCustomerClient(backendHttpClient)
    val userClient = BackendUserClient(backendHttpClient)
    val stationClient = BackendStationClient(backendHttpClient)
    val channelClient = BackendChannelClient(backendHttpClient)
    val measurementClient = BackendMeasurementClient(backendHttpClient)
    val recordsClient = BackendRecordsClient(backendHttpClient)
    val dslClient = BackendDslClient(backendHttpClient)
    val simulatorClient = BackendSimulatorClient(backendHttpClient)
    val statsClient = BackendStatsClient(backendHttpClient)
    val dataImportClient = BackendDataImportClient(backendHttpClient)

    val stationService = StationService(stationClient)
    val channelService = ChannelService(channelClient)
    val measurementService = MeasurementService(measurementClient)

    return ApiDependencies(
        jwtTokenService = jwtTokenService,
        authService = AuthService(
            backendAuthClient = authClient,
            jwtTokenService = jwtTokenService
        ),
        customerService = CustomerService(customerClient),
        userService = UserService(userClient),
        stationService = stationService,
        channelService = channelService,
        measurementService = measurementService,
        recordsService = RecordsService(recordsClient),
        dslService = DslService(dslClient),
        simulatorService = SimulatorService(simulatorClient),
        statsService = StatsService(statsClient),
        dataImportService = DataImportService(dataImportClient)
    )
}