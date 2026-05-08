package si.sensum.sws.parser

import org.w3c.dom.Element
import si.sensum.shared.models.api.measurements.MeasurementDto
import java.io.ByteArrayInputStream
import java.time.OffsetDateTime
import javax.xml.parsers.DocumentBuilderFactory

internal object SwsMeasurementsResponseParser {

    fun xmlToMeasurements(xml: String): List<MeasurementDto> {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = false

        val builder = factory.newDocumentBuilder()
        val document = builder.parse(ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8)))

        val measurementNodes = document.getElementsByTagName("Measurements")
        val result = mutableListOf<MeasurementDto>()

        for (i in 0 until measurementNodes.length) {
            val node = measurementNodes.item(i)

            if (node is Element) {
                val stationId = node.getTagValue("StationID")?.toLongOrNull() ?: continue
                val channelId = node.getTagValue("ChannelID")?.toIntOrNull() ?: continue
                val dateTimeString = node.getTagValue("DateTime") ?: continue
                val dateTime = OffsetDateTime.parse(dateTimeString)
                val value = node.getTagValue("Value")?.toDoubleOrNull() ?: continue
                val status = node.getTagValue("Status")?.toIntOrNull() ?: continue

                result.add(
                    MeasurementDto(
                        stationId = stationId,
                        channelId = channelId,
                        dateTime = dateTime,
                        value = value,
                        status = status
                    )
                )
            }
        }

        return result
    }

    private fun Element.getTagValue(tagName: String): String? {
        val nodes = this.getElementsByTagName(tagName)
        if (nodes.length == 0) return null
        return nodes.item(0)?.textContent?.trim()
    }
}