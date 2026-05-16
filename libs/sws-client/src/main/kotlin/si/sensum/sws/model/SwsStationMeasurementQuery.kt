package si.sensum.sws.model

internal data class SwsStationMeasurementQuery(
    val stationId: Long,
    val range: SwsDateTimeRange
) {
    fun asParameters(): List<Pair<String, String>> {
        return listOf(
            "StationID" to stationId.toString(),
            "datetimeFrom" to range.from,
            "datetimeTo" to range.to
        )
    }
}