package si.sensum.demo.screens.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.compose.PlotPanel
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.components.layout.ScreenContainer
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.theme.letsPlotTheme
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.components.ui.SensumEmptyState
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

    LaunchedEffect(Unit) {
        isLoading = true
        errorMsg = ""

        runCatching { apiClient.getMeasurements() }.fold(
            onSuccess = { loadedMeasurements -> measurements = loadedMeasurements },
            onFailure = { error -> errorMsg = "API Error: ${error.message}" }
        )

        isLoading = false
    }

    when {
        isLoading -> SensumEmptyState(
            icon = Res.drawable.chart,
            title = "Loading",
            subtitle = "Fetching measurements through API Gateway."
        )

        errorMsg.isNotEmpty() -> SensumEmptyState(
            icon = Res.drawable.chart,
            title = "Error",
            subtitle = errorMsg
        )

        measurements.isEmpty() -> SensumEmptyState(
            icon = Res.drawable.chart,
            title = "No data",
            subtitle = "Load measurements in Data first."
        )

        else -> ChartContent(measurements)
    }
}

@Composable
private fun ChartContent(
    measurements: List<MeasurementUi>
) {
    ScreenContainer(
        scrollable = true,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(SensumSpacing.lg)
    ) {
        Text(
            text = "${measurements.size} measurements",
            style = MaterialTheme.typography.titleMedium,
            color = SensumThemeColors.onSurface
        )

        HorizontalDivider(color = SensumThemeColors.border)

        SensumCard {
            Text(
                text = "Combined by channel",
                style = MaterialTheme.typography.titleSmall,
                color = SensumThemeColors.onSurface
            )
            LetsPlotPanel(
                measurements = measurements,
                title = "All measurements by channel",
                colorField = "channel",
                groupField = "channel"
            )
        }

        SensumCard {
            Text(
                text = "Separated by source",
                style = MaterialTheme.typography.titleSmall,
                color = SensumThemeColors.onSurface
            )
            LetsPlotPanel(
                measurements = measurements,
                title = "Measurements by source",
                colorField = "source",
                groupField = "source"
            )
        }

        SensumCard {
            Text(
                text = "Channel + source",
                style = MaterialTheme.typography.titleSmall,
                color = SensumThemeColors.onSurface
            )
            LetsPlotPanel(
                measurements = measurements,
                title = "Channel/source overview",
                colorField = "channelSource",
                groupField = "channelSource",
                points = true
            )
        }
    }
}

@Composable
private fun LetsPlotPanel(
    measurements: List<MeasurementUi>,
    title: String,
    colorField: String,
    groupField: String,
    points: Boolean = false
) {
    val plotTheme = letsPlotTheme()
    val sorted = measurements.sortedWith(compareBy<MeasurementUi> { it.channelId }.thenBy { it.dateTime })

    val data = mapOf(
        "time" to sorted.map { it.dateTime.toString() },
        "value" to sorted.map { it.value },
        "channel" to sorted.map { "${it.channelId} - ${it.channelName}" },
        "source" to sorted.map { it.source.name },
        "channelSource" to sorted.map { "${it.source.name} · ${it.channelId}" }
    )

    var plot = ggplot(data) {
        x = "time"
        y = "value"
        color = colorField
        group = groupField
    } + geomLine() +
            ggtitle(title) +
            labs(x = "Time", y = "Value", color = colorField) +
            plotTheme

    if (points) {
        plot += geomPoint(size = 1.5, alpha = 0.75)
    }

    PlotPanel(
        figure = plot,
        modifier = Modifier.height(360.dp),
        computationMessagesHandler = {}
    )
}
