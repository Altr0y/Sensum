package si.sensum.demo.model

object DemoChannels {
    const val DEFAULT_STATION_ID: Int = 2241
    const val DEFAULT_STATION_NAME: String = "Radar test"

    val names: Map<Int, String> = mapOf(
        127 to "L8001H - globina voda-radar [m]",
        128 to "L8001H - višina vode [m]",
        129 to "L8001H - globina vodnjaka (PPI220) [m]",
        130 to "PPI220 - Nivo [m]",
        131 to "PPI220 - Temperatura [°C]",
        132 to "PPI220 - globina vode-nivo [m]",
        133 to "L8001H - globina vode-nivo [-]",
        134 to "L8001H - Nivo [-]",
        135 to "L8001H - 4 [-]"
    )

    fun nameOf(channelId: Int): String {
        return names[channelId] ?: "Channel $channelId"
    }
}