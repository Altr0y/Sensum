package si.sensum.demo.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.compose.PlotPanel
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import si.sensum.demo.components.EmptyState
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.model.Measurement
import si.sensum.demo.repository.PostgresMeasurementRepository
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.chart

private val dbRepository = PostgresMeasurementRepository()

@Composable
fun MeasurementChart() {
    var measurements by remember { mutableStateOf<List<Measurement>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        dbRepository.getAll().fold(
            onSuccess = { measurements = it },
            onFailure = { errorMsg = "DB Error: ${it.message}" }
        )
        isLoading = false
    }

    when {
        isLoading -> {
            EmptyState(
                icon = Res.drawable.chart,
                title = "Loading...",
                subtitle = "Fetching measurements from database."
            )
        }
        errorMsg.isNotEmpty() -> {
            EmptyState(
                icon = Res.drawable.chart,
                title = "Error",
                subtitle = errorMsg
            )
        }
        measurements.isEmpty() -> {
            EmptyState(
                icon = Res.drawable.chart,
                title = "No data to display",
                subtitle = "Load measurements in Data Loader\nand save them to the database first."
            )
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Measurement Chart — ${measurements.size} measurements",
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider(color = SensumThemeColors.border)
                LetsPlotMeasurementChart(
                    measurements = measurements,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun LetsPlotMeasurementChart(
    measurements: List<Measurement>,
    modifier: Modifier = Modifier
) {
    val sorted = measurements.sortedWith(
        compareBy<Measurement> { it.channelName }.thenBy { it.dateTime }
    )

    val data = mapOf(
        "time"    to sorted.map { it.dateTime.toString() },
        "value"   to sorted.map { it.value },
        "channel" to sorted.map { "${it.channelId} - ${it.channelName}" }
    )

    val plot = ggplot(data) {
        x = "time"
        y = "value"
        color = "channel"
        group = "channel"
    } +
            geomLine(size = 1.2) +
            ggtitle("Measurements by channel") +
            labs(x = "Time", y = "Value", color = "Channel")

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        PlotPanel(
            figure = plot,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            computationMessagesHandler = {}
        )
    }
}