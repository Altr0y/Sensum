package si.sensum.sws

data class SwsConfig(
    val baseUrl: String,
    val timeoutMillis: Long = 10000
)