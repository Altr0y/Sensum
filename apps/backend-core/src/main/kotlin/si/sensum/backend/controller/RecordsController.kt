package si.sensum.backend.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import si.sensum.backend.domain.records.FilterOperator
import si.sensum.backend.domain.records.RecordFilter
import si.sensum.backend.domain.records.RecordsQuery
import si.sensum.backend.domain.records.SortDirection
import si.sensum.backend.service.RecordsService
import si.sensum.shared.ktor.response.respondOk

private const val MAX_PAGE_SIZE = 100

fun Route.configureRecordsRoutes(
    recordsService: RecordsService
) {
    route("/api/v1/records") {
        get("/stations") {
            call.respondOk {
                recordsService.getStations(
                    call.parseRecordsQuery(defaultSortBy = "stationId")
                )
            }
        }

        get("/channels") {
            call.respondOk {
                recordsService.getChannels(
                    call.parseRecordsQuery(defaultSortBy = "channelId")
                )
            }
        }

        get("/measurements") {
            call.respondOk {
                recordsService.getMeasurements(
                    call.parseRecordsQuery(defaultSortBy = "dateTime")
                )
            }
        }
    }
}

private fun ApplicationCall.parseRecordsQuery(
    defaultSortBy: String
): RecordsQuery {
    val query = request.queryParameters

    val page = query["page"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val pageSize = query["pageSize"]?.toIntOrNull()?.coerceIn(1, MAX_PAGE_SIZE) ?: 10

    val sortBy = query["sortBy"]
        ?.takeIf { it.isNotBlank() }
        ?: defaultSortBy

    val sortDirection = when (query["sortDirection"]?.lowercase()) {
        "desc" -> SortDirection.DESC
        else -> SortDirection.ASC
    }

    val filters = query.getAll("filter")
        .orEmpty()
        .mapNotNull(::parseFilter)

    return RecordsQuery(
        page = page,
        pageSize = pageSize,
        sortBy = sortBy,
        sortDirection = sortDirection,
        filters = filters
    )
}

private fun parseFilter(raw: String): RecordFilter? {
    val parts = raw.split(":", limit = 3)

    if (parts.size != 3) {
        return null
    }

    val field = parts[0].trim()
    val operator = when (parts[1].trim().lowercase()) {
        "eq" -> FilterOperator.EQ
        "contains" -> FilterOperator.CONTAINS
        "gt" -> FilterOperator.GT
        "lt" -> FilterOperator.LT
        "gte" -> FilterOperator.GTE
        "lte" -> FilterOperator.LTE
        else -> return null
    }
    val value = parts[2].trim()

    if (field.isBlank() || value.isBlank()) {
        return null
    }

    return RecordFilter(
        field = field,
        operator = operator,
        value = value
    )
}