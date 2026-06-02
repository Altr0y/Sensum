package si.sensum.sws.parser

import org.w3c.dom.Element
import si.sensum.shared.models.channels.ChannelDto

internal object SwsChannelsResponseParser {

    fun xmlToChannels(
        xml: String,
        stationId: Long
    ): List<ChannelDto> {
        val document = SwsXmlDocumentParser.parse(
            xml = xml,
            responseName = "channels"
        )

        return document
            .elementsByTagName("Channels")
            .mapNotNull { element ->
                element.toChannelOrNull(defaultStationId = stationId)
            }
    }

    private fun Element.toChannelOrNull(defaultStationId: Long): ChannelDto? {
        val channelId = optionalTag("ChannelID")?.toIntOrNull()
            ?: return null

        return ChannelDto(
            stationId = optionalTag("StationID")?.toLongOrNull() ?: defaultStationId,
            channelId = channelId,
            name = optionalTag("ChannelName"),
            sampleTime = optionalTag("SampleTime")?.toLongOrNull(),
            alarmLow = optionalTag("AlarmLow")?.toIntOrNull(),
            alarmHigh = optionalTag("AlarmHigh")?.toIntOrNull(),
            alarmHyst = optionalTag("AlarmHyst")?.toIntOrNull(),
            kota = optionalTag("Kota")?.toDoubleOrNull(),
            kotaSign = optionalTag("KotaSign")?.toDoubleOrNull(),
            description = optionalTag("Description"),
            unit = optionalTag("Unit")
        )
    }
}