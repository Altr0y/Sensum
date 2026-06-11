package si.sensum.demo.util

import si.sensum.shared.models.datetime.ApiDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeFormat {
    val DISPLAY_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm")

    fun LocalDateTime.toApiString(): String {
        return ApiDateTime.formatAppLocal(this)
    }

    fun LocalDateTime.toDisplayString(): String {
        return format(DISPLAY_FORMATTER)
    }

    fun String.parseApiDateTime(): LocalDateTime {
        return ApiDateTime.parseToAppLocal(this)
    }
}