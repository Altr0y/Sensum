package si.sensum.gm.validation

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import si.sensum.gm.errors.InvalidRequiredFieldException
import si.sensum.gm.errors.MissingRequiredFieldException
import si.sensum.shared.models.api.measurements.MeasurementRangeQuery
import si.sensum.shared.models.api.measurements.MeasurementsByStationChannelPairsRequest
import si.sensum.shared.models.datetime.ApiDateTime

internal fun ApplicationCall.receiveMeasurementRangeQuery(): MeasurementRangeQuery {
    return MeasurementRangeQuery(
        datetimeFrom = requireNormalizedQueryParameter("datetimeFrom"),
        datetimeTo = requireNormalizedQueryParameter("datetimeTo")
    )
}

internal suspend fun ApplicationCall.receiveStationChannelPairsMeasurementRequest(): MeasurementsByStationChannelPairsRequest {
    val request = receive<MeasurementsByStationChannelPairsRequest>()

    if (request.stationChannelPairs.isEmpty()) {
        throw MissingRequiredFieldException("stationChannelPairs")
    }

    return request.copy(
        datetimeFrom = requireNormalizedField(
            fieldName = "datetimeFrom",
            value = request.datetimeFrom
        ),
        datetimeTo = requireNormalizedField(
            fieldName = "datetimeTo",
            value = request.datetimeTo
        )
    )
}

private fun ApplicationCall.requireNormalizedQueryParameter(fieldName: String): String {
    val value = request.queryParameters[fieldName]

    if (value.isNullOrBlank()) {
        throw MissingRequiredFieldException(fieldName)
    }

    return requireNormalizedField(
        fieldName = fieldName,
        value = value
    )
}

private fun requireNormalizedField(
    fieldName: String,
    value: String
): String {
    return try {
        ApiDateTime.requireNormalized(
            fieldName = fieldName,
            value = value
        )
    } catch (error: IllegalArgumentException) {
        throw InvalidRequiredFieldException(
            fieldName = fieldName,
            reason = error.message ?: "expected format ${ApiDateTime.DESCRIPTION}"
        )
    }
}