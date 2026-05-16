package si.sensum.sws.operation.measurements

import si.sensum.shared.models.api.measurements.StationChannelPairDto
import si.sensum.sws.model.SoapVersion
import si.sensum.sws.model.SwsDateTimeRange
import si.sensum.sws.model.SwsSoapOperation
import si.sensum.sws.operation.SwsOperationDefaults.SWS_NAMESPACE
import si.sensum.sws.operation.SwsOperationFactory
import si.sensum.sws.xml.XmlEscaper

internal object GetMeasurementsByStationChannelPairsOperation {

    private const val METHOD_NAME = "GetMeasurementsByStationChannelPairs"
    private val SOAP_VERSION = SoapVersion.SOAP_11

    fun create(
        stationChannelPairs: List<StationChannelPairDto>,
        range: SwsDateTimeRange
    ): SwsSoapOperation {
        require(stationChannelPairs.isNotEmpty()) {
            "At least one station/channel pair is required."
        }

        return SwsOperationFactory.createWithBody(
            methodName = METHOD_NAME,
            soapVersion = SOAP_VERSION,
            body = buildBody(
                stationChannelPairs = stationChannelPairs,
                range = range
            )
        )
    }

    private fun buildBody(
        stationChannelPairs: List<StationChannelPairDto>,
        range: SwsDateTimeRange
    ): String {
        return """
            <$METHOD_NAME xmlns="$SWS_NAMESPACE">
                <StationChannelPairs>
${buildStationChannelPairsXml(stationChannelPairs).prependIndent("                    ")}
                </StationChannelPairs>
                <datetimeFrom>${XmlEscaper.escape(range.from)}</datetimeFrom>
                <datetimeTo>${XmlEscaper.escape(range.to)}</datetimeTo>
            </$METHOD_NAME>
        """.trimIndent()
    }

    private fun buildStationChannelPairsXml(
        stationChannelPairs: List<StationChannelPairDto>
    ): String {
        return stationChannelPairs.joinToString(separator = "\n") { pair ->
            """
            <StationChannelPair>
                <StationID>${pair.stationId}</StationID>
                <ChannelID>${pair.channelId}</ChannelID>
            </StationChannelPair>
            """.trimIndent()
        }
    }
}