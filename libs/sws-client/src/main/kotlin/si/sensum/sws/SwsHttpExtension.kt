package si.sensum.sws

import io.ktor.client.request.*
import io.ktor.http.*
import si.sensum.sws.model.SoapVersion

internal fun HttpRequestBuilder.applySoapHeaders(
    action: String,
    soapVersion: SoapVersion
) {
    contentType(ContentType.parse("${soapVersion.contentType}; charset=utf-8"))

    if (soapVersion.requiresSoapActionHeader()) {
        header("SOAPAction", "\"$action\"")
    }
}

private fun SoapVersion.requiresSoapActionHeader(): Boolean {
    return this == SoapVersion.SOAP_11
}