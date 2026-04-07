package feri.um.si.data_player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feri.um.si.data_player.conversions.XMLConverter
import java.io.File

@Composable
fun App(){
    val xml = File("measurement.xml").readText()
    val measurements = XMLConverter.xmlToMeasurements(xml)

    val backToXml = XMLConverter.measurementsToXml(measurements.take(3))
    println("BACK TO XML:\n$backToXml") //prints to console

    Column(modifier = Modifier.padding(16.dp)){
        Text("Rows: ${measurements.size}")
        measurements.take(5).forEach {
            Text(it.toString())
        }
    }
}