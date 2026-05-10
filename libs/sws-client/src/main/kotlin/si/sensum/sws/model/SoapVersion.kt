package si.sensum.sws.model

enum class SoapVersion(
    val envelopeNamespace: String,
    val contentType: String
) {
    SOAP_11(
        envelopeNamespace = "http://schemas.xmlsoap.org/soap/envelope/",
        contentType = "text/xml"
    ),

    SOAP_12(
        envelopeNamespace = "http://www.w3.org/2003/05/soap-envelope",
        contentType = "application/soap+xml"
    )
}