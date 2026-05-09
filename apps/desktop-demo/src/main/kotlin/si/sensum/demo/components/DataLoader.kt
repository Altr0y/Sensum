package si.sensum.demo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import si.sensum.demo.model.Measurement
import si.sensum.demo.model.MeasurementRequest
import si.sensum.demo.model.StationChannelPair
import si.sensum.demo.repository.MockMeasurementRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val STATION_ID = 2241
private val CHANNELS = mapOf(
    127 to "L8001H - globina voda-radar [m]",
    128 to "L8001H - višina vode [m]",
    129 to "L8001H - globina vodnjaka (PPI220) [m]",
    130 to "PPI220 - Nivo [m]",
    131 to "PPI220 - Temperatura [°C]",
    132 to "PPI220 - globina vode-nivo [m]",
    133 to "L8001H - globina vode-nivo [-]",
    134 to "L8001H - Nivo [-]",
    135 to "L8001H - 4 [-]"
)
private val DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val repository = MockMeasurementRepository()

@Composable
fun DataLoader(onMeasurementsLoaded: (List<Measurement>) -> Unit = {}) {
    val scope = rememberCoroutineScope()

    val selectedChannels = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            CHANNELS.keys.forEach { put(it, false) }
        }
    }

    var datetimeFrom by remember { mutableStateOf("2026-01-01 00:00:00") }
    var datetimeTo by remember { mutableStateOf("2026-01-01 01:00:00") }
    var fromError by remember { mutableStateOf(false) }
    var toError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMsg by remember { mutableStateOf("") }

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
            color = SensumColors.Muted
        )

        HorizontalDivider(color = SensumColors.Border)

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
                        selectedContainerColor = SensumColors.AccentMuted,
                        selectedLabelColor = SensumColors.Accent,
                        labelColor = SensumColors.Muted
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        selectedBorderColor = SensumColors.Accent,
                        borderColor = SensumColors.Border
                    )
                )
            }
        }

        HorizontalDivider(color = SensumColors.Border)

        // Date / time range
        Text("Time Range", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = datetimeFrom,
                onValueChange = { datetimeFrom = it; fromError = false },
                label = { Text("From") },
                placeholder = { Text("yyyy-MM-dd HH:mm:ss") },
                isError = fromError,
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = outlinedTextFieldColors(),
                supportingText = {
                    Text(
                        if (fromError) "Invalid format!" else "Format: yyyy-MM-dd HH:mm:ss",
                        color = if (fromError) SensumColors.Error else SensumColors.Muted
                    )
                }
            )
            OutlinedTextField(
                value = datetimeTo,
                onValueChange = { datetimeTo = it; toError = false },
                label = { Text("To") },
                placeholder = { Text("yyyy-MM-dd HH:mm:ss") },
                isError = toError,
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = outlinedTextFieldColors(),
                supportingText = {
                    Text(
                        if (toError) "Invalid format!" else "Format: yyyy-MM-dd HH:mm:ss",
                        color = if (toError) SensumColors.Error else SensumColors.Muted
                    )
                }
            )
        }

        HorizontalDivider(color = SensumColors.Border)

        // Load button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val from = parseDateTime(datetimeFrom).also { fromError = it == null }
                    val to = parseDateTime(datetimeTo).also { toError = it == null }
                    val pairs = selectedChannels.filter { it.value }.keys
                        .map { StationChannelPair(STATION_ID, it) }

                    if (from == null || to == null) {
                        statusMsg = "Error: check date format."
                        return@Button
                    }
                    if (pairs.isEmpty()) {
                        statusMsg = "Select at least one channel."
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        statusMsg = ""
                        val result = repository.getMeasurements(
                            MeasurementRequest(pairs, from, to)
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
                colors = ButtonDefaults.buttonColors(containerColor = SensumColors.Accent)
            ) {
                Text("Load Measurements", color = SensumColors.OnAccent)
            }

            if (isLoading) CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = SensumColors.Accent,
                strokeWidth = 2.dp
            )

            if (statusMsg.isNotEmpty()) Text(
                text = statusMsg,
                style = MaterialTheme.typography.bodyMedium,
                color = if (statusMsg.startsWith("Error")) SensumColors.Error else SensumColors.Success
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
            selectedContainerColor = SensumColors.AccentMuted,
            selectedLabelColor = SensumColors.Accent,
            labelColor = SensumColors.Muted
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = SensumColors.Accent,
            borderColor = SensumColors.Border
        )
    )
}

private fun parseDateTime(s: String): LocalDateTime? = try {
    LocalDateTime.parse(s, DATETIME_FORMAT)
} catch (e: DateTimeParseException) {
    null
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SensumColors.Accent,
    unfocusedBorderColor = SensumColors.Border,
    focusedLabelColor = SensumColors.Accent,
    unfocusedLabelColor = SensumColors.Muted,
    cursorColor = SensumColors.Accent,
    focusedTextColor = SensumColors.OnSurface,
    unfocusedTextColor = SensumColors.OnSurface,
)