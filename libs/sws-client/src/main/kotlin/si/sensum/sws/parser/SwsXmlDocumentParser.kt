package si.sensum.sws.parser

import org.w3c.dom.Document
import si.sensum.sws.SwsInvalidResponseException
import java.io.ByteArrayInputStream
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

internal object SwsXmlDocumentParser {

    fun parse(
        xml: String,
        responseName: String
    ): Document {
        return runCatching {
            DocumentBuilderFactory
                .newInstance()
                .apply {
                    configureSecureXmlParsing()
                }
                .newDocumentBuilder()
                .parse(ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8)))
        }.getOrElse { error ->
            throw SwsInvalidResponseException(
                "Invalid SWS $responseName XML response: ${error.message ?: error::class.simpleName}"
            )
        }
    }

    private fun DocumentBuilderFactory.configureSecureXmlParsing() {
        isNamespaceAware = true
        isXIncludeAware = false
        isExpandEntityReferences = false

        runCatching {
            setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        }

        runCatching {
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }

        runCatching {
            setFeature("http://xml.org/sax/features/external-general-entities", false)
        }

        runCatching {
            setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        }

        runCatching {
            setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        }
    }
}