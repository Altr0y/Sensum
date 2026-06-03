package si.sensum.shared.models.common

import kotlinx.serialization.Serializable

@Serializable
enum class DataSourceDto {
    UNKNOWN,
    SWS,
    MANUAL,
    SIM,
    DSL
}