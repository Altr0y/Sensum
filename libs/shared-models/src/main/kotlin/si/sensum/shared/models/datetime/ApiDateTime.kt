package si.sensum.shared.models.datetime

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

object ApiDateTime {
    val APP_ZONE: ZoneId = ZoneId.of("Europe/Ljubljana")
    val FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    const val DESCRIPTION: String = "yyyy-MM-dd'T'HH:mm:ssXXX"

    fun parse(value: String): OffsetDateTime {
        return OffsetDateTime.parse(value.trim(), FORMATTER)
    }

    fun format(value: OffsetDateTime): String {
        return value
            .truncatedTo(ChronoUnit.SECONDS)
            .format(FORMATTER)
    }

    fun normalize(value: String): String {
        return format(parse(value))
    }

    fun requireNormalized(
        fieldName: String,
        value: String
    ): String {
        if (value.isBlank()) {
            throw IllegalArgumentException("Missing required field: $fieldName")
        }

        return try {
            normalize(value)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "Invalid field '$fieldName': expected format $DESCRIPTION"
            )
        }
    }

    fun fromAppLocal(value: LocalDateTime): OffsetDateTime {
        return value
            .atZone(APP_ZONE)
            .toOffsetDateTime()
            .truncatedTo(ChronoUnit.SECONDS)
    }

    fun formatAppLocal(value: LocalDateTime): String {
        return format(fromAppLocal(value))
    }

    fun toAppLocal(value: OffsetDateTime): LocalDateTime {
        return value
            .atZoneSameInstant(APP_ZONE)
            .toLocalDateTime()
    }

    fun parseToAppLocal(value: String): LocalDateTime {
        return toAppLocal(parse(value))
    }

    fun isValid(value: String): Boolean {
        return try {
            parse(value)
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }
}