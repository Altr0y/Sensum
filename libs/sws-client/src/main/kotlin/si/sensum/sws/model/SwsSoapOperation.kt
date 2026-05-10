package si.sensum.sws.model

data class SwsSoapOperation(
    val name: String,
    val action: String,
    val xmlEnvelope: String,
    val soapVersion: SoapVersion = SoapVersion.SOAP_11
)