package si.sensum.logging

private const val SHORT_REQUEST_ID_LENGTH = 18

fun String?.shortRequestId(): String {
    if (this.isNullOrBlank()) {
        return "-"
    }

    return if (length <= SHORT_REQUEST_ID_LENGTH) {
        this
    } else {
        take(SHORT_REQUEST_ID_LENGTH)
    }
}