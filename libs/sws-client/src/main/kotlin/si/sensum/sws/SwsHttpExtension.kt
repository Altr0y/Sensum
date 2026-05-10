package si.sensum.sws

import io.ktor.client.request.*
import io.ktor.http.*
import si.sensum.sws.model.SoapVersion

fun HttpRequestBuilder.applySoapHeaders(
    action: String,
    soapVersion: SoapVersion
) {
    when (soapVersion) {
        SoapVersion.SOAP_11 -> {
            contentType(ContentType.Text.Xml.withCharset(Charsets.UTF_8))
            header("SOAPAction", "\"$action\"")
        }

        SoapVersion.SOAP_12 -> {
            contentType(ContentType.parse("application/soap+xml; charset=utf-8"))
        }
    }
}