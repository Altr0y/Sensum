package si.sensum.demo.screens.data

import si.sensum.demo.util.DateTimeFormat.toApiString
import java.time.LocalDateTime

internal fun LocalDateTime.toDataApiString(): String {
    return toApiString()
}