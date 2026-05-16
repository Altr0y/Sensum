package si.sensum.sws.model

import io.ktor.http.Headers

internal data class SwsSoapResponse(
    val body: String,
    val headers: Headers
)