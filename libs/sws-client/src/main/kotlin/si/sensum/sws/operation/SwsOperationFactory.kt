package si.sensum.sws.operation

import si.sensum.sws.model.SoapVersion
import si.sensum.sws.model.SwsSoapOperation
import si.sensum.sws.operation.SwsOperationDefaults.SWS_NAMESPACE
import si.sensum.sws.xml.SoapEnvelopeBuilder
import si.sensum.sws.xml.XmlEscaper

internal object SwsOperationFactory {

    fun create(
        methodName: String,
        soapVersion: SoapVersion = SoapVersion.SOAP_11,
        parameters: List<Pair<String, String>> = emptyList()
    ): SwsSoapOperation {
        val body = buildMethodBody(
            methodName = methodName,
            parameters = parameters
        )

        return SwsSoapOperation(
            name = methodName,
            action = "$SWS_NAMESPACE$methodName",
            xmlEnvelope = SoapEnvelopeBuilder.build(
                bodyContent = body,
                soapVersion = soapVersion
            ),
            soapVersion = soapVersion
        )
    }

    private fun buildMethodBody(
        methodName: String,
        parameters: List<Pair<String, String>>
    ): String {
        if (parameters.isEmpty()) {
            return """<$methodName xmlns="$SWS_NAMESPACE" />"""
        }

        val parameterXml = parameters.joinToString(separator = "\n") { (name, value) ->
            "    <$name>${XmlEscaper.escape(value)}</$name>"
        }

        return """<$methodName xmlns="$SWS_NAMESPACE">
$parameterXml
</$methodName>"""
    }
}