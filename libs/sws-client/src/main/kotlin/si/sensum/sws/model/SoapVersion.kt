package si.sensum.sws.model

internal enum class SoapVersion(
    val envelopePrefix: String,
    val envelopeNamespace: String,
    val contentType: String
) {
    SOAP_11(
        envelopePrefix = "soap",
        envelopeNamespace = "http://schemas.xmlsoap.org/soap/envelope/",
        contentType = "text/xml"
    ),

    SOAP_12(
        envelopePrefix = "soap12",
        envelopeNamespace = "http://www.w3.org/2003/05/soap-envelope",
        contentType = "application/soap+xml"
    )
}