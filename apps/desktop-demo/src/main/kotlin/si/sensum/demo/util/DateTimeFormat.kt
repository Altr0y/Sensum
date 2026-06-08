package si.sensum.demo.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/** Unified date/time utilities used across the entire desktop-demo module. */
object DateTimeFormat {

    /** ISO-8601 local format used for API query parameters: `2026-01-01T00:00:00` */
    val API_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    /** Human-readable format used in UI labels: `01.01.2026  00:00` */
    val DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm")

    fun LocalDateTime.toApiString(): String = format(API_FORMATTER)

    fun LocalDateTime.toDisplayString(): String = format(DISPLAY_FORMATTER)

    /**
     * Parses both local (`2026-01-01T00:00:00`) and offset-aware (`2026-02-01T00:00:00+01:00`)
     * datetime strings. Offset-aware values are normalised to UTC before conversion.
     * Handles the SWS XML format that includes timezone offsets.
     */
    fun String.parseApiDateTime(): LocalDateTime {
        return try {
            LocalDateTime.parse(this, API_FORMATTER)
        } catch (_: DateTimeParseException) {
            OffsetDateTime.parse(this)
                .withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDateTime()
        }
    }
}
