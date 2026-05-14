package si.sensum.demo.components.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.model.Measurement
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.chart
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.compose.PlotPanel
import si.sensum.demo.components.EmptyState
import si.sensum.demo.components.theme.SensumColors
import si.sensum.demo.components.theme.SensumThemeColors

@Composable
fun MeasurementChart(measurements: List<Measurement> = emptyList()) {
    if (measurements.isEmpty()) {
        EmptyState(
            icon = Res.drawable.chart,
            title = "No data to display",
            subtitle = "Go to Data Loader and load\nmeasurements first."
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "MeasurementChart - ${measurements.size} measurements",
            style = MaterialTheme.typography.titleLarge
        )

        HorizontalDivider(color = SensumThemeColors.border)

        if (measurements.isEmpty()) {
            Text(
                text = "No measurements loaded.",
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.muted
            )
            return@Column
        }

        LetsPlotMeasurementChart(
            measurements = measurements,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun LetsPlotMeasurementChart(
    measurements: List<Measurement>,
    modifier: Modifier = Modifier
) {
    val sorted = measurements.sortedWith(
        compareBy<Measurement> { it.channelName }
            .thenBy { it.dateTime }
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
            labs(
                x = "Time",
                y = "Value",
                color = "Channel"
            )

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