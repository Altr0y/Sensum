package si.sensum.sws.parser

import org.w3c.dom.Element
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.sws.SwsInvalidResponseException
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

internal object SwsMeasurementsResponseParser {
    private val swsLocalFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun xmlToMeasurements(xml: String, elementName: String = "Measurements"): List<MeasurementDto> {
        val document = SwsXmlDocumentParser.parse(
            xml = xml,
            responseName = "measurements"
        )
        return document
            .elementsByTagName(elementName)
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
        val trimmed = value.trim()

        return try {
            ApiDateTime.parse(trimmed)
        } catch (_: DateTimeParseException) {
            parseLegacyLocalDateTime(trimmed)
        } catch (_: IllegalArgumentException) {
            parseLegacyLocalDateTime(trimmed)
        }
    }

    private fun parseLegacyLocalDateTime(value: String): OffsetDateTime {
        val localDateTime = try {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (_: DateTimeParseException) {
            try {
                LocalDateTime.parse(value, swsLocalFormatter)
            } catch (_: DateTimeParseException) {
                throw SwsInvalidResponseException(
                    "Invalid SWS DateTime value: '$value'. Expected ISO offset datetime or legacy SWS local datetime."
                )
            }
        }

        return ApiDateTime.fromAppLocal(localDateTime)
    }
}