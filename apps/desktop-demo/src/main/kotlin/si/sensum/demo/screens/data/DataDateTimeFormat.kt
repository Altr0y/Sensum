package si.sensum.demo.screens.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal val ApiLocalDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ISO_LOCAL_DATE_TIME

internal fun LocalDateTime.toApiString(): String {
    return format(ApiLocalDateTimeFormatter)
}