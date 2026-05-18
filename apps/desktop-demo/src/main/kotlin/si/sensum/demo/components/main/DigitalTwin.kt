package si.sensum.demo.components.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.letsPlot.compose.PlotPanel
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.gggrid
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.themes.flavorDarcula
import si.sensum.demo.components.DateTimePicker
import si.sensum.demo.components.EmptyState
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.model.Measurement
import si.sensum.demo.model.MeasurementRequest
import si.sensum.demo.model.StationChannelPair
import si.sensum.demo.repository.BackendMeasurementRepository
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.digital_twin
import java.time.LocalDateTime
import org.jetbrains.letsPlot.themes.flavorStandard
import si.sensum.demo.components.theme.LocalIsDarkTheme

private const val STATION_ID = 2241
private val ALL_CHANNELS = listOf(127, 128, 129, 130, 131, 132, 133, 134)
private val repository = BackendMeasurementRepository()

@Composable
fun DigitalTwin() {
    val scope = rememberCoroutineScope()

    var datetimeFrom by remember { mutableStateOf(LocalDateTime.of(2026, 6, 1, 0, 0)) }
    var datetimeTo by remember { mutableStateOf(LocalDateTime.of(2026, 6, 7, 0, 0)) }
    var measurements by remember { mutableStateOf<List<Measurement>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMsg by remember { mutableStateOf("") }
    var hasLoaded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Digital Twin", style = MaterialTheme.typography.titleLarge)
        Text(
            "Simulated data for RadarTest station - pick time interval",
            style = MaterialTheme.typography.bodyMedium,
            color = SensumThemeColors.muted
        )

        HorizontalDivider(color = SensumThemeColors.border)

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateTimePicker(
                label = "From",
                value = datetimeFrom,
                onValueChange = { datetimeFrom = it }
            )
            DateTimePicker(
                label = "To",
                value = datetimeTo,
                onValueChange = { datetimeTo = it }
            )

            Button(
                onClick = {
                    if (!datetimeFrom.isBefore(datetimeTo)) {
                        statusMsg = "Error: 'From' has to be before 'To'."
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        statusMsg = ""
                        val pairs = ALL_CHANNELS.map { StationChannelPair(STATION_ID, it) }
                        repository.regenerateAndGet(
                            MeasurementRequest(pairs, datetimeFrom, datetimeTo)
                        ).fold(
                            onSuccess = {
                                measurements = it
                                hasLoaded = true
                                statusMsg = "Loaded ${it.size} measurements."
                            },
                            onFailure = {
                                statusMsg = "Error: ${it.message}"
                            }
                        )
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.accent)
            ) {
                Text("Generiraj", color = SensumThemeColors.onAccent)
            }

            if (isLoading) CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = SensumThemeColors.accent,
                strokeWidth = 2.dp
            )

            if (statusMsg.isNotEmpty()) Text(
                text = statusMsg,
                style = MaterialTheme.typography.bodyMedium,
                color = if (statusMsg.startsWith("Error")) SensumThemeColors.error
                else SensumThemeColors.success
            )
        }

        HorizontalDivider(color = SensumThemeColors.border)

        if (!hasLoaded) {
            EmptyState(
                icon = Res.drawable.digital_twin,
                title = "No data",
                subtitle = "Pick time interval and Generate."
            )
        } else if (measurements.isEmpty()) {
            EmptyState(
                icon = Res.drawable.digital_twin,
                title = "No results",
                subtitle = "Backend did not return any measurements for this interval."
            )
        } else {
            DigitalTwinChart(
                measurements = measurements,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun DigitalTwinChart(
    measurements: List<Measurement>,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val sorted = measurements.sortedWith(
        compareBy<Measurement> { it.channelId }.thenBy { it.dateTime }
    )

    fun buildPlot(title: String, channelIds: List<Int>): Plot {
        val filtered = sorted.filter { it.channelId in channelIds }
        val data = mapOf(
            "time" to filtered.map { it.dateTime.toString() },
            "value" to filtered.map { it.value },
            "channel" to filtered.map { "${it.channelId} - ${it.channelName}" }
        )
        return ggplot(data) {
            x = "time"
            y = "value"
            color = "channel"
            group = "channel"
        } + geomLine(size = 1.0, alpha = 0.8) +
                ggtitle(title) +
                labs(x = "", y = "Vrednost", color = "Kanal") +
                if (isDark) flavorDarcula() else flavorStandard()
    }

    val grid = gggrid(
        plots = listOf(
            buildPlot("Temperatura [°C]", listOf(131)),
            buildPlot("Globina vode [m]", listOf(127, 132, 133)),
            buildPlot("Višina / Nivo [m]", listOf(128, 130, 134)),
            buildPlot("Globina vodnjaka [m]", listOf(129))
        ),
        ncol = 2
    ) + if (isDark) flavorDarcula() else flavorStandard()

    PlotPanel(
        figure = grid,
        modifier = modifier.padding(12.dp),
        computationMessagesHandler = {}
    )
}