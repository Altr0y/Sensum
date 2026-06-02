package si.sensum.demo.components.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.letsPlot.compose.PlotPanel
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.gggrid
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.components.DateTimePicker
import si.sensum.demo.components.EmptyState
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.theme.letsPlotTheme
import si.sensum.demo.model.MeasurementRequestUi
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.model.StationChannelPairUi
import si.sensum.demo.repository.ApiMeasurementRepository
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.digital_twin
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private const val STATION_ID = 2241
private val ALL_CHANNELS = listOf(127, 128, 129, 130, 131, 132, 133, 134)

@Composable
fun DigitalTwin(
    apiClient: SensumApiClient
) {
    val scope = rememberCoroutineScope()
    val repository = remember(apiClient) {
        ApiMeasurementRepository(apiClient)
    }
    var datetimeFrom by remember { mutableStateOf(LocalDateTime.of(2026, 6, 1, 0, 0)) }
    var datetimeTo by remember { mutableStateOf(LocalDateTime.of(2026, 6, 7, 0, 0)) }
    var measurements by remember { mutableStateOf<List<MeasurementUi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMsg by remember { mutableStateOf("") }
    var hasLoaded by remember { mutableStateOf(false) }
    var isLive by remember { mutableStateOf(false) }
    var liveMeasurements by remember { mutableStateOf<List<MeasurementUi>>(emptyList()) }
    var liveCurrentTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var isPaused by remember { mutableStateOf(false) }

    fun validateDateRange(
        from: LocalDateTime,
        to: LocalDateTime
    ): Boolean {
        if (from.isBefore(to)) {
            return true
        }

        statusMsg = "Error: 'From' has to be before 'To'."
        return false
    }

    fun loadMeasurements(
        from: LocalDateTime,
        to: LocalDateTime
    ) {
        if (!validateDateRange(from, to)) {
            return
        }

        scope.launch {
            isLoading = true
            statusMsg = ""

            val pairs = ALL_CHANNELS.map { channelId ->
                StationChannelPairUi(STATION_ID, channelId)
            }

            repository.regenerateAndGet(
                MeasurementRequestUi(
                    pairs = pairs,
                    datetimeFrom = from,
                    datetimeTo = to
                )
            ).fold(
                onSuccess = { loadedMeasurements ->
                    measurements = loadedMeasurements
                    hasLoaded = true
                    statusMsg = "Loaded ${loadedMeasurements.size} measurements."
                },
                onFailure = { error ->
                    statusMsg = "Error: ${error.message}"
                }
            )

            isLoading = false
        }
    }

    LaunchedEffect(isLive, measurements) {
        if (!isLive) return@LaunchedEffect

        liveMeasurements = emptyList()
        liveCurrentTime = null

        val sortedMeasurements = measurements.sortedBy { it.dateTime }

        for (measurement in sortedMeasurements) {
            if (!isLive) break

            while (isPaused && isLive) {
                delay(200L)
            }

            liveMeasurements = liveMeasurements + measurement
            liveCurrentTime = measurement.dateTime

            delay(1000L)
        }

        isLive = false
        isPaused = false
    }

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
                    loadMeasurements(
                        from = datetimeFrom,
                        to = datetimeTo
                    )
                },
                enabled = !isLoading && measurements.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.accent)
            ) {
                Text("Generiraj", color = SensumThemeColors.onAccent)
            }

            Button(
                onClick = {
                    val wholeYearFrom = LocalDateTime.of(2026, 1, 1, 0, 0)
                    val wholeYearTo = LocalDateTime.of(2026, 12, 31, 23, 0)

                    datetimeFrom = wholeYearFrom
                    datetimeTo = wholeYearTo

                    loadMeasurements(
                        from = wholeYearFrom,
                        to = wholeYearTo
                    )
                },
                enabled = !isLoading && measurements.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.info)
            ) {
                Text("Whole Year", color = SensumThemeColors.onAccent)
            }

            if (!isLive) {
                Button(
                    onClick = {
                        if (!validateDateRange(datetimeFrom, datetimeTo)) {
                            return@Button
                        }
                        isPaused = false
                        isLive = true
                    },
                    enabled = !isLoading && measurements.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.success)
                ) {
                    Text("Live", color = SensumThemeColors.onAccent)
                }
            } else {
                // Pause / Resume
                Button(
                    onClick = { isPaused = !isPaused },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPaused) SensumThemeColors.success else SensumThemeColors.info
                    )
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        modifier = Modifier.size(25.dp)
                    )
                }

                // Stop
                Button(
                    onClick = {
                        isLive = false
                        isPaused = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.error)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        modifier = Modifier.size(25.dp)
                    )
                }

                // Reset
                Button(
                    onClick = {
                        liveMeasurements = emptyList()
                        liveCurrentTime = null
                        isPaused = false
                        isLive = false
                        scope.launch {
                            delay(100L)
                            isLive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.muted)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.size(25.dp)
                    )
                }
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

        if (isLive && liveMeasurements.isNotEmpty()) {
            liveCurrentTime?.let {
                Text(
                    "${if (isPaused) "⏸ Paused" else "▶ Live"}: ${it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))} — vsaka sekunda = 1 ura",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPaused) SensumThemeColors.warning else SensumThemeColors.success
                )
            }
            DigitalTwinChart(
                measurements = liveMeasurements,
                modifier = Modifier.fillMaxSize()
            )
        } else if (!hasLoaded) {
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
    measurements: List<MeasurementUi>,
    modifier: Modifier = Modifier
) {
    val plotTheme = letsPlotTheme()

    val sorted = measurements.sortedWith(
        compareBy<MeasurementUi> { it.channelId }.thenBy { it.dateTime }
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
                plotTheme
    }

    val grid = gggrid(
        plots = listOf(
            buildPlot("Temperatura [°C]", listOf(131)),
            buildPlot("Globina vode [m]", listOf(127, 132, 133)),
            buildPlot("Višina / Nivo [m]", listOf(128, 130, 134)),
            buildPlot("Globina vodnjaka [m]", listOf(129))
        ),
        ncol = 2
    ) + plotTheme

    PlotPanel(
        figure = grid,
        modifier = modifier.padding(12.dp),
        computationMessagesHandler = {}
    )
}

private fun channelName(channelId: Int): String = when (channelId) {
    127 -> "L8001H - globina voda-radar [m]"
    128 -> "L8001H - višina vode [m]"
    129 -> "L8001H - globina vodnjaka (PPI220) [m]"
    130 -> "PPI220 - Nivo [m]"
    131 -> "PPI220 - Temperatura [°C]"
    132 -> "PPI220 - globina vode-nivo [m]"
    133 -> "L8001H - globina vode-nivo [-]"
    134 -> "L8001H - Nivo [-]"
    else -> "Unknown ($channelId)"
}