package si.sensum.gm.config

import si.sensum.sws.SwsConfig

data class GmAppConfig(
    val sws: SwsConfig,
    val auth: GmAuthConfig
)

data class GmAuthConfig(
    val tokenTtlHours: Long,
    val apiToken: String
)