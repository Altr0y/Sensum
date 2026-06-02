package si.sensum.gm.config

import si.sensum.sws.SwsConfig

internal data class GmAppConfig(
    val sws: SwsConfig,
    val auth: GmAuthConfig
)

internal data class GmAuthConfig(
    val tokenTtlHours: Long,
    val serviceJwtSecret: String,
    val serviceJwtIssuer: String,
    val serviceJwtAudience: String,
    val serviceJwtRealm: String
)