package si.sensum.sws.parser

import org.w3c.dom.Document
import org.w3c.dom.Element
import si.sensum.sws.SwsInvalidResponseException

internal fun Document.elementsByTagName(name: String): List<Element> {
    val nodes = getElementsByTagName(name)

    return buildList {
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)

            if (node is Element) {
                add(node)
            }
        }
    }
}

internal fun Element.optionalTag(name: String): String? {
    val nodes = getElementsByTagName(name)

    if (nodes.length == 0) {
        return null
    }

    return nodes
        .item(0)
        ?.textContent
        ?.trim()
        ?.takeIf { it.isNotBlank() }
}

internal fun Element.requireTag(name: String): String {
    return optionalTag(name)
        ?: throw SwsInvalidResponseException(
            "Invalid SWS XML: missing or blank <$name>"
        )
}

internal fun Element.requireLongTag(name: String): Long {
    val value = requireTag(name)

    return value.toLongOrNull()
        ?: throw SwsInvalidResponseException(
            "Invalid SWS XML: <$name> must be a Long, got '$value'"
        )
}

internal fun Element.requireIntTag(name: String): Int {
    val value = requireTag(name)

    return value.toIntOrNull()
        ?: throw SwsInvalidResponseException(
            "Invalid SWS XML: <$name> must be an Int, got '$value'"
        )
}

internal fun Element.requireDoubleTag(name: String): Double {
    val value = requireTag(name)

    return value.toDoubleOrNull()
        ?: throw SwsInvalidResponseException(
            "Invalid SWS XML: <$name> must be a Double, got '$value'"
        )
}