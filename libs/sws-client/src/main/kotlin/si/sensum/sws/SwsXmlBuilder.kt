package si.sensum.sws

object SwsXmlBuilder {

    fun buildEnvelope(bodyContent: String): String =
        """<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope
    xmlns:xsi="${SwsConstants.XML_SCHEMA_INSTANCE}"
    xmlns:xsd="${SwsConstants.XML_SCHEMA}"
    xmlns:soap="${SwsConstants.SOAP_ENVELOPE_NAMESPACE}">
    <soap:Body>
${bodyContent.prependIndent("        ")}
    </soap:Body>
</soap:Envelope>"""

    fun buildLoginRequest(username: String, password: String): String {
        val body =
            """<Login xmlns="${SwsConstants.SWS_NAMESPACE}">
    <UserName>${escapeXml(username)}</UserName>
    <Password>${escapeXml(password)}</Password>
</Login>"""

        return buildEnvelope(body)
    }

    private fun escapeXml(value: String): String =
        value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
}