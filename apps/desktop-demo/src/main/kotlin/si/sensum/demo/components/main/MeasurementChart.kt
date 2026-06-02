package si.sensum.demo.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.compose.PlotPanel
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.components.EmptyState
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.theme.letsPlotTheme
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.chart

@Composable
fun MeasurementChart(
    apiClient: SensumApiClient = remember { SensumApiClient() }
) {
    var measurements by remember { mutableStateOf<List<MeasurementUi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    // Meritve naloži prek API Gateway.
    LaunchedEffect(Unit) {
        isLoading = true
        errorMsg = ""

        runCatching {
            apiClient.getMeasurements()
        }.fold(
            onSuccess = { loadedMeasurements ->
                measurements = loadedMeasurements
            },
            onFailure = { error ->
                errorMsg = "API Error: ${error.message}"
            }
        )

        isLoading = false
    }

    when {
        isLoading -> {
            EmptyState(
                icon = Res.drawable.chart,
                title = "Loading...",
                subtitle = "Fetching measurements through API Gateway."
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
                subtitle = "Load measurements in Data Loader or refresh them from API Gateway."
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
    measurements: List<MeasurementUi>,
    modifier: Modifier = Modifier
) {
//    val isDark = LocalIsDarkTheme.current
    val plotTheme = letsPlotTheme()

    val sorted = measurements.sortedWith(
        compareBy<MeasurementUi> { it.channelName }.thenBy { it.dateTime }
    )

    val data = mapOf(
        "time" to sorted.map { it.dateTime.toString() },
        "value" to sorted.map { it.value },
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
            labs(x = "Time", y = "Value", color = "Channel") +
            plotTheme

    PlotPanel(
        figure = plot,
        modifier = modifier.padding(12.dp),
        computationMessagesHandler = {}
    )
}