package si.sensum.transform

import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Element
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

private fun sanitizeXmlTag(header: String): String {
    return header.trim()
        .replace(" ", "_")
        .replace("[", "")
        .replace("]", "")
        .replace("/", "_per_")
        .replace(Regex("[^a-zA-Z0-9_\\-.]"), "_")
        .let { if (it[0].isDigit()) "_$it" else it }
}

object CsvConverter {

    //CSV -> JSON
    fun csvToJson(csv: String): String {
        val reader = CSVReader(StringReader(csv))
        val rows = reader.readAll()
        if (rows.isEmpty()) return "[]"

        val headers = rows[0]
        val jsonArray = JSONArray()

        for (i in 1 until rows.size) {
            val obj = JSONObject()
            headers.forEachIndexed { index, header ->
                obj.put(header.trim(), rows[i].getOrElse(index) { "" }.trim())
            }
            jsonArray.put(obj)
        }

        return jsonArray.toString(2)
    }

    //CSV -> XML
    fun csvToXml(csv: String, rootElement: String = "data", rowElement: String = "row"): String {
        val reader = CSVReader(StringReader(csv))
        val rows = reader.readAll()
        if (rows.isEmpty()) return "<$rootElement/>"

        val headers = rows[0]
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.newDocument()
        val root = doc.createElement(rootElement)
        doc.appendChild(root)

        for (i in 1 until rows.size) {
            val rowEl = doc.createElement(rowElement)
            headers.forEachIndexed { index, header ->
                val cell = doc.createElement(sanitizeXmlTag(header))
                cell.textContent = rows[i].getOrElse(index) { "" }.trim()
                rowEl.appendChild(cell)
            }
            root.appendChild(rowEl)
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        val writer = StringWriter()
        transformer.transform(DOMSource(doc), StreamResult(writer))
        return writer.toString()
    }

    // JSON -> CSV
    fun jsonToCsv(json: String): String {
        val jsonArray = JSONArray(json)
        if (jsonArray.length() == 0) return ""

        val writer = StringWriter()
        val csvWriter = CSVWriter(writer)

        val headers = jsonArray.getJSONObject(0).keys().asSequence().toList()
        csvWriter.writeNext(headers.toTypedArray())

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val row = headers.map { obj.optString(it, "") }.toTypedArray()
            csvWriter.writeNext(row)
        }

        csvWriter.close()
        return writer.toString()
    }

    // XML → CSV
    fun xmlToCsv(xml: String): String {
        val doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(xml.byteInputStream())

        val rows = doc.documentElement.childNodes
        if (rows.length == 0) return ""

        val headers = mutableListOf<String>()
        val firstRow = rows.item(0) as? Element ?: return ""
        val children = firstRow.childNodes
        for (i in 0 until children.length) {
            headers.add(children.item(i).nodeName)
        }

        val writer = StringWriter()
        val csvWriter = CSVWriter(writer)
        csvWriter.writeNext(headers.toTypedArray())

        for (i in 0 until rows.length) {
            val row = rows.item(i) as? Element ?: continue
            val values = headers.map {
                row.getElementsByTagName(it).item(0)?.textContent ?: ""
            }.toTypedArray()
            csvWriter.writeNext(values)
        }

        csvWriter.close()
        return writer.toString()
    }
}