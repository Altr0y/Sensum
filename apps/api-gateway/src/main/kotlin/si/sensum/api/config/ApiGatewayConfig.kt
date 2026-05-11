package si.sensum.api.config

data class ApiGatewayConfig(
    val backendCoreBaseUrl: String,
    val demoUsername: String,
    val demoPassword: String,
    val demoAuthToken: String
)