package si.sensum.sws.xml

import si.sensum.sws.model.SoapVersion

object SoapEnvelopeBuilder {

    private const val XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance"
    private const val XML_SCHEMA = "http://www.w3.org/2001/XMLSchema"

    fun build(
        bodyContent: String,
        soapVersion: SoapVersion = SoapVersion.SOAP_11
    ): String {
        val prefix = when (soapVersion) {
            SoapVersion.SOAP_11 -> "soap"
            SoapVersion.SOAP_12 -> "soap12"
        }

        return """<?xml version="1.0" encoding="utf-8"?>
<$prefix:Envelope
    xmlns:xsi="$XML_SCHEMA_INSTANCE"
    xmlns:xsd="$XML_SCHEMA"
    xmlns:$prefix="${soapVersion.envelopeNamespace}">
    <$prefix:Body>
${bodyContent.prependIndent("        ")}
    </$prefix:Body>
</$prefix:Envelope>"""
    }
}