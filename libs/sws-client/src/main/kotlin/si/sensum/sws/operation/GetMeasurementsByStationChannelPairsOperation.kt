package si.sensum.sws.operation

import si.sensum.shared.models.api.measurements.StationChannelPairDto
import si.sensum.sws.model.SoapVersion
import si.sensum.sws.model.SwsSoapOperation
import si.sensum.sws.operation.SwsOperationDefaults.SWS_NAMESPACE
import si.sensum.sws.xml.SoapEnvelopeBuilder
import si.sensum.sws.xml.XmlEscaper

object GetMeasurementsByStationChannelPairsOperation {

    private const val METHOD_NAME = "GetMeasurementsByStationChannelPairs"

    fun create(
        stationChannelPairs: List<StationChannelPairDto>,
        datetimeFrom: String,
        datetimeTo: String
    ): SwsSoapOperation {
        val pairsXml = stationChannelPairs.joinToString(separator = "\n") { pair ->
            """
            <StationChannelPair>
                <StationID>${pair.stationId}</StationID>
                <ChannelID>${pair.channelId}</ChannelID>
            </StationChannelPair>
            """.trimIndent()
        }

        val body = """
            <$METHOD_NAME xmlns="$SWS_NAMESPACE">
                <StationChannelPairs>
                    $pairsXml
                </StationChannelPairs>
                <datetimeFrom>${XmlEscaper.escape(datetimeFrom)}</datetimeFrom>
                <datetimeTo>${XmlEscaper.escape(datetimeTo)}</datetimeTo>
            </$METHOD_NAME>
        """.trimIndent()

        return SwsSoapOperation(
            name = METHOD_NAME,
            action = "$SWS_NAMESPACE$METHOD_NAME",
            xmlEnvelope = SoapEnvelopeBuilder.build(
                bodyContent = body,
                soapVersion = SoapVersion.SOAP_12
            ),
            soapVersion = SoapVersion.SOAP_12
        )
    }
}