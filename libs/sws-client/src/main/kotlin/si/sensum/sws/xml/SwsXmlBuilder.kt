package si.sensum.sws.xml

import si.sensum.sws.SwsConstants

object SwsXmlBuilder {

    fun buildLoginRequest(
        username: String,
        password: String
    ): String {
        val body =
            """<Login xmlns="${SwsConstants.SWS_NAMESPACE}">
    <UserName>${escapeXml(username)}</UserName>
    <Password>${escapeXml(password)}</Password>
</Login>"""

        return buildEnvelope(body)
    }

    fun buildGetAllMeasurementsRequest(
        datetimeFrom: String,
        datetimeTo: String
    ): String {
        val body =
            """<GetAllMeasurements xmlns="${SwsConstants.SWS_NAMESPACE}">
    <datetimeFrom>${escapeXml(datetimeFrom)}</datetimeFrom>
    <datetimeTo>${escapeXml(datetimeTo)}</datetimeTo>
</GetAllMeasurements>"""

        return buildEnvelope(body)
    }

    private fun buildEnvelope(bodyContent: String): String =
        """<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope
    xmlns:xsi="${SwsConstants.XML_SCHEMA_INSTANCE}"
    xmlns:xsd="${SwsConstants.XML_SCHEMA}"
    xmlns:soap="${SwsConstants.SOAP_ENVELOPE_NAMESPACE}">
    <soap:Body>
${bodyContent.prependIndent("        ")}
    </soap:Body>
</soap:Envelope>"""

    private fun escapeXml(value: String): String =
        value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
}