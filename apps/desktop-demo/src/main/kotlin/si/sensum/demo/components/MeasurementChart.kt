package si.sensum.demo.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import si.sensum.demo.model.Measurement
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.chart
@Composable
fun MeasurementChart(measurements: List<Measurement> = emptyList()) {
    if (measurements.isEmpty()) {
        EmptyState(
            icon = Res.drawable.chart,
            title = "No data to display",
            subtitle = "Go to Data Loader and load\nmeasurements first."
        )
    } else {
        Text("MeasurementChart - ${measurements.size} measurements")
    }
}