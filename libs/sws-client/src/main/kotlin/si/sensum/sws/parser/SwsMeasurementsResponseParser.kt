package si.sensum.sws.parser

import org.w3c.dom.Element
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.sws.SwsInvalidResponseException
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

internal object SwsMeasurementsResponseParser {

    fun xmlToMeasurements(xml: String): List<MeasurementDto> {
        val document = SwsXmlDocumentParser.parse(
            xml = xml,
            responseName = "measurements"
        )

        return document
            .elementsByTagName("Measurements")
            .map(::parseMeasurement)
    }

    private fun parseMeasurement(element: Element): MeasurementDto {
        return MeasurementDto(
            stationId = element.requireLongTag("StationID"),
            channelId = element.requireIntTag("ChannelID"),
            dateTime = parseDateTime(element.requireTag("DateTime")),
            value = element.requireDoubleTag("Value"),
            status = element.requireIntTag("Status")
        )
    }

    private fun parseDateTime(value: String): OffsetDateTime {
        return try {
            ApiDateTime.parseFlexibleOffset(value)
        } catch (_: DateTimeParseException) {
            throw SwsInvalidResponseException(
                "Invalid SWS DateTime value: '$value'. Expected date-time with optional offset, for example ${ApiDateTime.OFFSET_EXAMPLE}, ${ApiDateTime.LOCAL_EXAMPLE} or ${ApiDateTime.SWS_LOCAL_EXAMPLE}"
            )
        }
    }
}