package si.sensum.sws

import io.ktor.client.request.*
import io.ktor.http.*

fun HttpRequestBuilder.applySoapHeaders(action: String) {
    contentType(ContentType.Text.Xml.withCharset(Charsets.UTF_8))
    header("SOAPAction", action)
}