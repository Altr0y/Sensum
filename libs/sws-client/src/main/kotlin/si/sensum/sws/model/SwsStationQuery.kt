package si.sensum.sws.model

internal data class SwsStationQuery(
    val stationId: Long
) {
    fun asParameters(): List<Pair<String, String>> {
        return listOf("StationID" to stationId.toString())
    }
}