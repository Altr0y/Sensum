package si.sensum.sws.model

internal data class SwsChannelQuery(
    val stationId: Long,
    val channelId: Int,
    val range: SwsDateTimeRange
) {
    fun asParameters(): List<Pair<String, String>> {
        return listOf(
            "StationID" to stationId.toString(),
            "ChannelID" to channelId.toString(),
            "datetimeFrom" to range.from,
            "datetimeTo" to range.to
        )
    }
}