package si.sensum.demo.screens.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.compose.PlotPanel
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomBoxplot
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.components.datetime.DateTimePicker
import si.sensum.demo.components.layout.ScreenContainer
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.theme.letsPlotTheme
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.components.ui.SensumEmptyState
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.chart
import java.time.LocalDateTime
import org.jetbrains.letsPlot.Stat

@Composable
fun MeasurementChart(
    apiClient: SensumApiClient = remember { SensumApiClient() }
) {
    var allMeasurements by remember { mutableStateOf<List<MeasurementUi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    var dateFrom by remember { mutableStateOf(LocalDateTime.now().minusDays(30)) }
    var dateTo by remember { mutableStateOf(LocalDateTime.now()) }
    var filterActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMsg = ""
        runCatching { apiClient.getMeasurements() }.fold(
            onSuccess = { allMeasurements = it },
            onFailure = { errorMsg = "API Error: ${it.message}" }
        )
        isLoading = false
    }

    val measurements by remember(allMeasurements, filterActive, dateFrom, dateTo) {
        derivedStateOf {
            if (!filterActive) {
                allMeasurements
            } else {
                allMeasurements.filter { m ->
                    !m.dateTime.isBefore(dateFrom) && !m.dateTime.isAfter(dateTo)
                }
            }
        }
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

        allMeasurements.isEmpty() -> SensumEmptyState(
            icon = Res.drawable.chart,
            title = "No data",
            subtitle = "Load measurements in Data first."
        )

        else -> ChartContent(
            measurements = measurements,
            allCount = allMeasurements.size,
            dateFrom = dateFrom,
            dateTo = dateTo,
            filterActive = filterActive,
            onDateFrom = { dateFrom = it },
            onDateTo = { dateTo = it },
            onApplyFilter = { filterActive = true },
            onClearFilter = { filterActive = false }
        )
    }
}

@Composable
private fun ChartContent(
    measurements: List<MeasurementUi>,
    allCount: Int,
    dateFrom: LocalDateTime,
    dateTo: LocalDateTime,
    filterActive: Boolean,
    onDateFrom: (LocalDateTime) -> Unit,
    onDateTo: (LocalDateTime) -> Unit,
    onApplyFilter: () -> Unit,
    onClearFilter: () -> Unit
) {
    ScreenContainer(
        scrollable = true,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(SensumSpacing.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.xs)) {
                Text(
                    text = if (filterActive) "${measurements.size} / $allCount measurements" else "$allCount measurements",
                    style = MaterialTheme.typography.titleMedium,
                    color = SensumThemeColors.onSurface
                )
                Text(
                    text = "${measurements.map { it.channelId }.distinct().size} channels · ${measurements.map { it.source.name }.distinct().size} sources",
                    style = MaterialTheme.typography.bodySmall,
                    color = SensumThemeColors.muted
                )
            }

            Spacer(Modifier.weight(1f))

            DateTimePicker(
                label = "From",
                value = dateFrom,
                onValueChange = onDateFrom
            )

            DateTimePicker(
                label = "To",
                value = dateTo,
                onValueChange = onDateTo
            )


            if (filterActive) {
                SensumButton(
                    text = "Clear",
                    onClick = onClearFilter,
                    variant = SensumButtonVariant.Ghost,
                    compact = true
                )
            }
        }

        HorizontalDivider(color = SensumThemeColors.border)

        if (measurements.isEmpty()) {
            SensumEmptyState(
                icon = Res.drawable.chart,
                title = "No data in range",
                subtitle = "Adjust the date range or clear the filter."
            )
            return@ScreenContainer
        }

        SensumCard {
            Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
                Text("Time series by channel", style = MaterialTheme.typography.titleSmall, color = SensumThemeColors.onSurface)
                Text("Value over time, coloured by channel", style = MaterialTheme.typography.bodySmall, color = SensumThemeColors.muted)
                TimeSeriesChart(measurements)
            }
        }

        SensumCard {
            Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
                Text("Value distribution per channel", style = MaterialTheme.typography.titleSmall, color = SensumThemeColors.onSurface)
                Text("Box-and-whisker: median, quartiles and outliers", style = MaterialTheme.typography.bodySmall, color = SensumThemeColors.muted)
                DistributionChart(measurements)
            }
        }

        SensumCard {
            Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
                Text("Average value per channel", style = MaterialTheme.typography.titleSmall, color = SensumThemeColors.onSurface)
                Text("Pre-aggregated mean, grouped by channel", style = MaterialTheme.typography.bodySmall, color = SensumThemeColors.muted)
                AverageBarChart(measurements)
            }
        }

        SensumCard {
            Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
                Text("Status distribution", style = MaterialTheme.typography.titleSmall, color = SensumThemeColors.onSurface)
                Text("Count of measurements per status code (0=OK, 1=Warning, 2=Critical, 3=Error)", style = MaterialTheme.typography.bodySmall, color = SensumThemeColors.muted)
                StatusBarChart(measurements)
            }
        }

        SensumCard {
            Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
                Text("Channel + source overview", style = MaterialTheme.typography.titleSmall, color = SensumThemeColors.onSurface)
                Text("Scatter plot: all measurements coloured by source", style = MaterialTheme.typography.bodySmall, color = SensumThemeColors.muted)
                ScatterBySourceChart(measurements)
            }
        }
    }
}

@Composable
private fun TimeSeriesChart(measurements: List<MeasurementUi>) {
    val theme = letsPlotTheme()
    val sorted = measurements.sortedWith(compareBy<MeasurementUi> { it.channelId }.thenBy { it.dateTime })
    val data = mapOf(
        "time" to sorted.map { it.dateTime.toString() },
        "value" to sorted.map { it.value },
        "channel" to sorted.map { "${it.channelId} · ${it.channelName}" }
    )
    val plot = ggplot(data) {
        x = "time"; y = "value"; color = "channel"; group = "channel"
    } + geomLine() + geomPoint(size = 1.0, alpha = 0.5) +
        ggtitle("Time series") +
        labs(x = "Date/time", y = "Value", color = "Channel") +
        theme

    PlotPanel(figure = plot, modifier = Modifier.height(360.dp), computationMessagesHandler = {})
}

@Composable
private fun DistributionChart(measurements: List<MeasurementUi>) {
    val theme = letsPlotTheme()
    val data = mapOf(
        "channel" to measurements.map { "${it.channelId} · ${it.channelName}" },
        "value" to measurements.map { it.value }
    )
    val plot = ggplot(data) {
        x = "channel"; y = "value"; fill = "channel"
    } + geomBoxplot(outlierSize = 1.5, alpha = 0.75) +
        ggtitle("Value distribution by channel") +
        labs(x = "Channel", y = "Value") +
        theme

    PlotPanel(figure = plot, modifier = Modifier.height(360.dp), computationMessagesHandler = {})
}

@Composable
private fun AverageBarChart(measurements: List<MeasurementUi>) {
    val theme = letsPlotTheme()
    val grouped = measurements
        .groupBy { "${it.channelId} · ${it.channelName}" }
        .entries
        .sortedBy { it.key }

    val data = mapOf(
        "channel" to grouped.map { it.key },
        "avg" to grouped.map { it.value.sumOf { m -> m.value } / it.value.size },
        "min" to grouped.map { entry -> entry.value.minOf { it.value } },
        "max" to grouped.map { entry -> entry.value.maxOf { it.value } },
        "count" to grouped.map { it.value.size }
    )
    val plot = ggplot(data) {
        x = "channel"; y = "avg"; fill = "channel"
    } + geomBar(stat = Stat.identity, alpha = 0.85) +
        ggtitle("Average value per channel") +
        labs(x = "Channel", y = "Average value") +
        theme

    PlotPanel(figure = plot, modifier = Modifier.height(320.dp), computationMessagesHandler = {})
}

@Composable
private fun StatusBarChart(measurements: List<MeasurementUi>) {
    val theme = letsPlotTheme()

    val statusLabels = mapOf(
        0 to "0 · OK",
        1 to "1 · Warning",
        2 to "2 · Critical",
        3 to "3 · Error"
    )

    val grouped = measurements
        .groupBy { it.status }
        .entries
        .sortedBy { it.key }

    val data = mapOf(
        "status" to grouped.map { statusLabels[it.key] ?: it.key.toString() },
        "count" to grouped.map { it.value.size }
    )

    val plot = ggplot(data) {
        x = "status"
        y = "count"
        fill = "status"
    } + geomBar(stat = Stat.identity) +
            ggtitle("Measurements by status") +
            labs(x = "Status", y = "Count") +
            theme

    PlotPanel(
        figure = plot,
        modifier = Modifier.height(300.dp),
        computationMessagesHandler = {}
    )
}

@Composable
private fun ScatterBySourceChart(measurements: List<MeasurementUi>) {
    val theme = letsPlotTheme()
    val sorted = measurements.sortedBy { it.dateTime }
    val data = mapOf(
        "time" to sorted.map { it.dateTime.toString() },
        "value" to sorted.map { it.value },
        "source" to sorted.map { it.source.name },
        "channel" to sorted.map { "${it.channelId} · ${it.channelName}" }
    )
    val plot = ggplot(data) {
        x = "time"; y = "value"; color = "source"; shape = "channel"
    } + geomPoint(size = 2.0, alpha = 0.75) +
        ggtitle("Scatter by source") +
        labs(x = "Date/time", y = "Value", color = "Source") +
        theme

    PlotPanel(figure = plot, modifier = Modifier.height(360.dp), computationMessagesHandler = {})
}
