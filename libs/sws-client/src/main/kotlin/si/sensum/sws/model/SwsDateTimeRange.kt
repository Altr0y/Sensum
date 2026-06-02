package si.sensum.sws.model

internal data class SwsDateTimeRange(
    val from: String,
    val to: String
) {
    fun asParameters(): List<Pair<String, String>> {
        return listOf(
            "datetimeFrom" to from,
            "datetimeTo" to to
        )
    }
}