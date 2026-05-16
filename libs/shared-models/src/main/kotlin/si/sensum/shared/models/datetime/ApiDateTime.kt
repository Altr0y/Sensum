package si.sensum.shared.models.datetime

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object ApiDateTime {
    val LOCAL_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val OFFSET_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    const val LOCAL_EXAMPLE: String = "2026-01-01T00:00:00"
    const val OFFSET_EXAMPLE: String = "2026-02-01T00:00:00+01:00"

    const val LOCAL_DESCRIPTION: String = "yyyy-MM-dd'T'HH:mm:ss"
    const val OFFSET_DESCRIPTION: String = "yyyy-MM-dd'T'HH:mm:ssXXX"

    val SWS_LOCAL_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    const val SWS_LOCAL_EXAMPLE: String = "2026-01-01 00:01:00"
    const val SWS_LOCAL_DESCRIPTION: String = "yyyy-MM-dd HH:mm:ss"

    fun parseLocal(value: String): LocalDateTime {
        return LocalDateTime.parse(value.trim(), LOCAL_FORMATTER)
    }

    fun formatLocal(value: LocalDateTime): String {
        return value.format(LOCAL_FORMATTER)
    }

    fun normalizeLocal(value: String): String {
        return formatLocal(parseLocal(value))
    }

    fun requireNormalizedLocal(
        fieldName: String,
        value: String
    ): String {
        if (value.isBlank()) {
            throw IllegalArgumentException("Missing required field: $fieldName")
        }

        return try {
            normalizeLocal(value)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "Invalid field '$fieldName': expected format $LOCAL_DESCRIPTION, for example $LOCAL_EXAMPLE"
            )
        }
    }

    fun parseOffset(value: String): OffsetDateTime {
        return OffsetDateTime.parse(value.trim(), OFFSET_FORMATTER)
    }

    fun formatOffset(value: OffsetDateTime): String {
        return value.format(OFFSET_FORMATTER)
    }

    fun normalizeOffset(value: String): String {
        return formatOffset(parseOffset(value))
    }

    fun requireNormalizedOffset(
        fieldName: String,
        value: String
    ): String {
        if (value.isBlank()) {
            throw IllegalArgumentException("Missing required field: $fieldName")
        }

        return try {
            normalizeOffset(value)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "Invalid field '$fieldName': expected format $OFFSET_DESCRIPTION, for example $OFFSET_EXAMPLE"
            )
        }
    }

    fun parseFlexibleOffset(
        value: String,
        defaultOffset: ZoneOffset = ZoneOffset.UTC
    ): OffsetDateTime {
        val trimmed = value.trim()

        return try {
            parseOffset(trimmed)
        } catch (_: DateTimeParseException) {
            try {
                parseLocal(trimmed).atOffset(defaultOffset)
            } catch (_: DateTimeParseException) {
                parseSwsLocal(trimmed).atOffset(defaultOffset)
            }
        }
    }

    fun isValidLocal(value: String): Boolean {
        return try {
            parseLocal(value)
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }

    fun isValidOffset(value: String): Boolean {
        return try {
            parseOffset(value)
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }

    fun isValidFlexibleOffset(value: String): Boolean {
        return try {
            parseFlexibleOffset(value)
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }

    val FORMATTER: DateTimeFormatter = LOCAL_FORMATTER

    const val EXAMPLE: String = LOCAL_EXAMPLE
    const val DESCRIPTION: String = LOCAL_DESCRIPTION

    fun parse(value: String): LocalDateTime {
        return parseLocal(value)
    }

    fun format(value: LocalDateTime): String {
        return formatLocal(value)
    }

    fun normalize(value: String): String {
        return normalizeLocal(value)
    }

    fun requireNormalized(
        fieldName: String,
        value: String
    ): String {
        return requireNormalizedLocal(
            fieldName = fieldName,
            value = value
        )
    }

    fun isValid(value: String): Boolean {
        return isValidLocal(value)
    }

    fun parseSwsLocal(value: String): LocalDateTime {
        return LocalDateTime.parse(value.trim(), SWS_LOCAL_FORMATTER)
    }

    fun formatSwsLocal(value: LocalDateTime): String {
        return value.format(SWS_LOCAL_FORMATTER)
    }
}