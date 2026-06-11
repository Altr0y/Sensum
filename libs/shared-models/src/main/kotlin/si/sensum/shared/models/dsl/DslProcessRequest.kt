package si.sensum.shared.models.dsl

import kotlinx.serialization.Serializable

@Serializable
data class DslProcessRequest(
    val source: String
)
