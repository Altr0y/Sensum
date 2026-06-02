package si.sensum.sws.parser

import org.w3c.dom.Element
import si.sensum.shared.models.stations.StationDto

internal object SwsStationsResponseParser {

    fun xmlToStations(xml: String): List<StationDto> {
        val document = SwsXmlDocumentParser.parse(
            xml = xml,
            responseName = "stations"
        )

        return document
            .elementsByTagName("Stations")
            .mapNotNull { element ->
                element.toStationOrNull()
            }
    }

    private fun Element.toStationOrNull(): StationDto? {
        val stationId = optionalTag("StationID")?.toLongOrNull()
            ?: return null

        return StationDto(
            stationId = stationId,
            name = optionalTag("StationName"),
            modbusAddress = optionalTag("ModbusAddress")?.toIntOrNull(),
            serialNumber = optionalTag("SerialNumber"),
            stationType = optionalTag("StationType"),
            description = optionalTag("Description"),
            latitude = optionalTag("Latitude")?.toDoubleOrNull(),
            longitude = optionalTag("Longitude")?.toDoubleOrNull()
        )
    }
}