package si.sensum.transform

import si.sensum.shared.models.measurements.Measurement
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.time.OffsetDateTime
import javax.xml.parsers.DocumentBuilderFactory

object XmlConverter {
    fun xmlToMeasurements(xml: String): List<Measurement>{
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = false

        val builder = factory.newDocumentBuilder()
        // document - whole xml script
        val document = builder.parse(ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8)))

        // measurementNodes - all <Measurement> ... </Measurement> tags
        val measurementNodes = document.getElementsByTagName("Measurements")
        val result = mutableListOf<Measurement>()

        for(i in 0 until measurementNodes.length){
            val node = measurementNodes.item(i)
            if(node is Element){
                val stationId = node.getTagValue("StationID")?.toLongOrNull() ?: continue
                val channelId = node.getTagValue("ChannelID")?.toIntOrNull() ?: continue
                val dateTimeString = node.getTagValue("DateTime") ?: continue
                val dateTime = OffsetDateTime.parse(dateTimeString)
                val value = node.getTagValue("Value")?.toDoubleOrNull() ?: continue
                val status = node.getTagValue("Status")?.toIntOrNull() ?: continue

                result.add(
                    Measurement(
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

    fun measurementsToXml(measurements: List<Measurement>): String {
        val rows = measurements.joinToString("\n"){
            measurement ->
            """
                <Measurements>
                    <StationID>${measurement.stationId}</StationID>
                    <ChannelID>${measurement.channelId}</ChannelID>
                    <DateTime>${measurement.dateTime}</DateTime>
                    <Value>${measurement.value}</Value>
                    <Status>${measurement.status}</Status>
                </Measurements>
            """.trimIndent()
        }

        return """
            <?xml version="1.0" encoding="utf-8"?>
            <DocumentElement>
            $rows
            </DocumentElement
        """.trimIndent()
    }

    // finds tag by name example "StationId" finds all <StationId>2241</StationID> and extracts value 2241
    private fun Element.getTagValue(tagName: String): String?{
        val nodes = this.getElementsByTagName(tagName)
        if(nodes.length == 0) return null
        return nodes.item(0)?.textContent?.trim()
    }
}