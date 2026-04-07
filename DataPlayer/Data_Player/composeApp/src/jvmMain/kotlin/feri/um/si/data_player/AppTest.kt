package feri.um.si.data_player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feri.um.si.data_player.conversions.JSONconverter
import feri.um.si.data_player.conversions.XMLConverter
import java.io.File

@Composable
fun AppTest(){
    val xml = File("measurement.xml").readText()
    val measurements = XMLConverter.xmlToMeasurements(xml)

    val backToXml = XMLConverter.measurementsToXml(measurements.take(3))
    println("BACK TO XML:\n$backToXml") //prints to console

    val json = JSONconverter.measurementsToJson(measurements.take(3))
    println(json)

    val parsedBack = JSONconverter.jsonToMeasurements(json)
    println(parsedBack)

    Column(modifier = Modifier.padding(16.dp)){
        Text("Rows: ${measurements.size}")
        for (measurement in measurements.take(5)) {
            Text(measurement.toString())
        }
    }
}