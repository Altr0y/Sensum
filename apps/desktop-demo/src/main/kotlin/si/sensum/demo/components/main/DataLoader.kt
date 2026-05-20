package si.sensum.demo.components.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import si.sensum.demo.components.DateTimePicker
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.model.DemoChannels
import si.sensum.demo.model.Measurement
import si.sensum.demo.model.MeasurementRequest
import si.sensum.demo.model.StationChannelPair
import si.sensum.demo.repository.MockMeasurementRepository
import si.sensum.demo.repository.SwsMeasurementRepository
import java.time.LocalDateTime

private const val STATION_ID = DemoChannels.DEFAULT_STATION_ID
private val CHANNELS = DemoChannels.names
private val repository = MockMeasurementRepository()
//private val repository = SwsMeasurementRepository()

@Composable
fun DataLoader(onMeasurementsLoaded: (List<Measurement>) -> Unit = {}) {
    val scope = rememberCoroutineScope()

    val selectedChannels = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            CHANNELS.keys.forEach { put(it, false) }
        }
    }

    var datetimeFrom by remember { mutableStateOf(LocalDateTime.of(2026, 1, 1, 0, 0)) }
    var datetimeTo   by remember { mutableStateOf(LocalDateTime.of(2026, 1, 1, 1, 0)) }
    var isLoading    by remember { mutableStateOf(false) }
    var statusMsg    by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Data Loader", style = MaterialTheme.typography.titleLarge)

        Text(
            "Station: $STATION_ID — Radar test",
            style = MaterialTheme.typography.bodyMedium,
            color = SensumThemeColors.muted
        )

        HorizontalDivider(color = SensumThemeColors.border)

        // Channels
        Text("Channels", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipToggle(
                label = "Select All",
                selected = selectedChannels.values.all { it },
                onClick = { CHANNELS.keys.forEach { selectedChannels[it] = true } }
            )
            FilterChipToggle(
                label = "Clear All",
                selected = selectedChannels.values.none { it },
                onClick = { CHANNELS.keys.forEach { selectedChannels[it] = false } }
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            CHANNELS.forEach { (id, name) ->
                val selected = selectedChannels[id] == true
                FilterChip(
                    selected = selected,
                    onClick = { selectedChannels[id] = !selected },
                    label = { Text("$id — $name", style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SensumThemeColors.accentMuted,
                        selectedLabelColor = SensumThemeColors.accent,
                        labelColor = SensumThemeColors.muted
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        selectedBorderColor = SensumThemeColors.accent,
                        borderColor = SensumThemeColors.border
                    )
                )
            }
        }

        HorizontalDivider(color = SensumThemeColors.border)

        // Date / time range
        Text("Time Range", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
        }

        HorizontalDivider(color = SensumThemeColors.border)

        // Load button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val pairs = selectedChannels.filter { it.value }.keys
                        .map { StationChannelPair(STATION_ID, it) }

                    if (pairs.isEmpty()) {
                        statusMsg = "Select at least one channel."
                        return@Button
                    }
                    if (!datetimeFrom.isBefore(datetimeTo)) {
                        statusMsg = "Error: 'From' must be before 'To'."
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        statusMsg = ""
                        val result = repository.getMeasurements(
                            MeasurementRequest(pairs, datetimeFrom, datetimeTo)
                        )
                        result.fold(
                            onSuccess = {
                                statusMsg = "Loaded ${it.size} measurements."
                                onMeasurementsLoaded(it)
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
                Text("Load Measurements", color = SensumThemeColors.onAccent)
            }

            if (isLoading) CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = SensumThemeColors.accent,
                strokeWidth = 2.dp
            )

            if (statusMsg.isNotEmpty()) Text(
                text = statusMsg,
                style = MaterialTheme.typography.bodyMedium,
                color = if (statusMsg.startsWith("Error")) SensumThemeColors.error else SensumThemeColors.success
            )
        }
    }
}

@Composable
private fun FilterChipToggle(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SensumThemeColors.accentMuted,
            selectedLabelColor = SensumThemeColors.accent,
            labelColor = SensumThemeColors.muted
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = SensumThemeColors.accent,
            borderColor = SensumThemeColors.border
        )
    )
}
