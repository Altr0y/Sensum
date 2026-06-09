package si.sensum.backend.controller

import io.ktor.server.application.ApplicationCall
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import si.sensum.shared.models.datetime.ApiDateTime

internal fun ApplicationCall.requireDateRangeQuery(): Pair<LocalDateTime, LocalDateTime> {
    val fromRaw = request.queryParameters["from"]
        ?: throw IllegalArgumentException("Missing required query parameter: from")

    val toRaw = request.queryParameters["to"]
        ?: throw IllegalArgumentException("Missing required query parameter: to")

    val from = ApiDateTime
        .parseToAppLocal(
            ApiDateTime.requireNormalized(
                fieldName = "from",
                value = fromRaw
            )
        )
        .toKotlinLocalDateTime()

    val to = ApiDateTime
        .parseToAppLocal(
            ApiDateTime.requireNormalized(
                fieldName = "to",
                value = toRaw
            )
        )
        .toKotlinLocalDateTime()

    return from to to
}