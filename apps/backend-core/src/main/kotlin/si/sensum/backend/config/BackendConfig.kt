package si.sensum.backend.config

data class BackendConfig(
    val gmBaseUrl: String,
    val gmApiAuthToken: String,
    val swsUsername: String,
    val swsPassword: String
)