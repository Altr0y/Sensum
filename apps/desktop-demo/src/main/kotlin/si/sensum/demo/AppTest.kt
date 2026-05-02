package si.sensum.demo

import si.sensum.transform.CsvConverter
import si.sensum.transform.JsonConverter
import si.sensum.transform.XmlConverter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File
import org.json.JSONArray
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun AppTest(){
    val xml = File("test-data/measurements/measurement.xml").readText()
    val measurements = XmlConverter.xmlToMeasurements(xml)

    val backToXml = XmlConverter.measurementsToXml(measurements.take(3))
    println("BACK TO XML:\n$backToXml") //prints to console

    val json = JsonConverter.measurementsToJson(measurements.take(3))
    println(json)

    val parsedBack = JsonConverter.jsonToMeasurements(json)
    println(parsedBack)

    // CSV tests
    val csv = File("test-data/measurements/TestData_010126_00-00_010126_01-00.csv").readText()
    val newJson = CsvConverter.csvToJson(csv)
    println("CSV TO JSON:\n$newJson")
    val backToCsv = CsvConverter.jsonToCsv(newJson)
    println("BACK TO CSV:\n$backToCsv")
    val newXml = CsvConverter.csvToXml(csv)
    println("CSV TO XML:\n$newXml")
    val backToCsv2 = CsvConverter.xmlToCsv(newXml)
    println("BACK TO CSV FROM XML:\n$backToCsv2")

    val jsonArray = JSONArray(newJson)

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        // kolegičin prikaz - nespremenjen
        item {
            Text(
                text = "XML meritve (${measurements.size} vrstic):",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        items(measurements.take(5)) { measurement ->
            Text(measurement.toString())
        }

        // tvoj CSV prikaz
        item {
            Text(
                text = "CSV podatki (${jsonArray.length()} vrstic):",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        items(minOf(5, jsonArray.length())) { i ->
            val obj = jsonArray.getJSONObject(i)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Postaja: ${obj.optString("Station Name")}")
                    Text("Kanal: ${obj.optString("Channel Name")}")
                    Text("Čas: ${obj.optString("Date Time")}")
                    Text("Vrednost: ${obj.optString("Value")}")
                }
            }
        }
    }


}