package si.sensum.demo.components.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.EmptyState
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.model.Measurement
import si.sensum.demo.repository.PostgresMeasurementRepository
import si.sensum.demo.resources.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val dbRepository = PostgresMeasurementRepository()

@Composable
fun DatabasePanel(measurements: List<Measurement> = emptyList()) {
    val scope = rememberCoroutineScope()

    var dbMeasurements by remember { mutableStateOf<List<Measurement>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMsg by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    // Naloži iz DB ob zagonu
    LaunchedEffect(Unit) {
        isLoading = true
        dbRepository.getAll().fold(
            onSuccess = { dbMeasurements = it },
            onFailure = { statusMsg = "DB Error: ${it.message}" }
        )
        isLoading = false
    }

    val grouped = dbMeasurements.groupBy { it.channelId }.toSortedMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Text("Database Panel", style = MaterialTheme.typography.titleLarge)
        Text(
            "${dbMeasurements.size} measurements across ${grouped.size} channels",
            style = MaterialTheme.typography.bodyMedium,
            color = SensumThemeColors.muted
        )

        HorizontalDivider(color = SensumThemeColors.border)

        // Toolbar
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shrani naložene iz DataLoaderja v DB
            Button(
                onClick = { showSaveDialog = true },
                enabled = measurements.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.accent)
            ) {
                Text("Save Loaded Data", color = SensumThemeColors.onAccent)
            }

            // Osveži iz DB
            OutlinedButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        dbRepository.getAll().fold(
                            onSuccess = { dbMeasurements = it; statusMsg = "Refreshed." },
                            onFailure = { statusMsg = "DB Error: ${it.message}" }
                        )
                        isLoading = false
                    }
                }
            ) {
                Text("Refresh", color = SensumThemeColors.muted)
            }

            // Izbriši vse
            OutlinedButton(
                onClick = { showDeleteAllDialog = true },
                enabled = dbMeasurements.isNotEmpty()
            ) {
                Text("Delete All", color = SensumThemeColors.error)
            }

            if (isLoading) CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = SensumThemeColors.accent,
                strokeWidth = 2.dp
            )

            if (statusMsg.isNotEmpty()) Text(
                text = statusMsg,
                style = MaterialTheme.typography.bodyMedium,
                color = if (statusMsg.startsWith("DB Error")) SensumThemeColors.error else SensumThemeColors.success
            )
        }

        HorizontalDivider(color = SensumThemeColors.border)

        // Content
        if (dbMeasurements.isEmpty() && !isLoading) {
            EmptyState(
                icon = Res.drawable.database_panel,
                title = "No data in database",
                subtitle = "Load measurements in Data Loader\nand press Save Loaded Data."
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.33f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grouped.forEach { (channelId, entries) ->
                    ChannelAccordion(
                        channelId = channelId,
                        entries = entries,
                        onUpdate = { updated ->
                            scope.launch {
                                dbRepository.update(updated).fold(
                                    onSuccess = {
                                        dbMeasurements = dbMeasurements.map { m ->
                                            if (m.id == it.id) it else m
                                        }
                                        statusMsg = "Updated."
                                    },
                                    onFailure = { statusMsg = "DB Error: ${it.message}" }
                                )
                            }
                        },
                        onDelete = { id ->
                            scope.launch {
                                dbRepository.delete(id).fold(
                                    onSuccess = {
                                        dbMeasurements = dbMeasurements.filter { it.id != id }
                                        statusMsg = "Deleted."
                                    },
                                    onFailure = { statusMsg = "DB Error: ${it.message}" }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    // Dialog: shrani DataLoader podatke v DB
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save to Database") },
            text = { Text("Save ${measurements.size} loaded measurements to the database?") },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        scope.launch {
                            isLoading = true
                            dbRepository.insertAll(measurements).fold(
                                onSuccess = { insertedCount ->
                                    statusMsg = "Saved $insertedCount measurements."

                                    dbRepository.getAll().onSuccess { loadedMeasurements ->
                                        dbMeasurements = loadedMeasurements
                                    }
                                },
                                onFailure = { error ->
                                    statusMsg = "DB Error: ${error.message}"
                                }
                            )
                            isLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.accent)
                ) { Text("Save", color = SensumThemeColors.onAccent) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSaveDialog = false }) { Text("Cancel") }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Dialog: izbriši vse
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Delete All") },
            text = { Text("This will permanently delete all ${dbMeasurements.size} measurements from the database.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAllDialog = false
                        scope.launch {
                            isLoading = true
                            dbRepository.deleteAll().fold(
                                onSuccess = { dbMeasurements = emptyList(); statusMsg = "All deleted." },
                                onFailure = { statusMsg = "DB Error: ${it.message}" }
                            )
                            isLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.error)
                ) { Text("Delete All", color = SensumThemeColors.onAccent) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteAllDialog = false }) { Text("Cancel") }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun ChannelAccordion(
    channelId: Int,
    entries: List<Measurement>,
    onUpdate: (Measurement) -> Unit,
    onDelete: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val channelName = entries.first().channelName

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (expanded) Res.drawable.chevron_down else Res.drawable.chevron_right
                        ),
                        contentDescription = null,
                        tint = SensumThemeColors.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "$channelId — $channelName",
                        style = MaterialTheme.typography.titleSmall,
                        color = SensumThemeColors.onSurface
                    )
                }
                Text(
                    "${entries.size} rows",
                    style = MaterialTheme.typography.labelMedium,
                    color = SensumThemeColors.muted
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    // Table header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        TableHeaderCell("DateTime", 3f)
                        TableHeaderCell("Value", 2f)
                        TableHeaderCell("Status", 1f)
                        Spacer(Modifier.width(60.dp))
                    }

                    HorizontalDivider(color = SensumThemeColors.border)

                    entries.forEachIndexed { i, m ->
                        EditableRow(
                            measurement = m,
                            isEven = i % 2 == 0,
                            onUpdate = onUpdate,
                            onDelete = onDelete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditableRow(
    measurement: Measurement,
    isEven: Boolean,
    onUpdate: (Measurement) -> Unit,
    onDelete: (Int) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var dtValue by remember { mutableStateOf(measurement.dateTime.format(DT_FORMAT)) }
    var valValue by remember { mutableStateOf(measurement.value.toString()) }
    var statusValue by remember { mutableStateOf(measurement.status.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isEven) MaterialTheme.colorScheme.background
                else MaterialTheme.colorScheme.surface
            )
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (editing) {
            InlineField(dtValue, Modifier.weight(3f)) { dtValue = it }
            InlineField(valValue, Modifier.weight(2f)) { valValue = it }
            InlineField(statusValue, Modifier.weight(1f)) { statusValue = it }
            Row(Modifier.width(60.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = {
                    val dt = try {
                        LocalDateTime.parse(dtValue, DT_FORMAT)
                    } catch (_: DateTimeParseException) {
                        null
                    }
                    val v = valValue.toDoubleOrNull()
                    val s = statusValue.toIntOrNull()
                    if (dt != null && v != null && s != null) {
                        onUpdate(measurement.copy(dateTime = dt, value = v, status = s))
                        editing = false
                    }
                }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        painterResource(Res.drawable.check),
                        null,
                        tint = SensumThemeColors.success,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = { editing = false }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        painterResource(Res.drawable.x),
                        null,
                        tint = SensumThemeColors.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else {
            TableCell(measurement.dateTime.format(DT_FORMAT), 3f)
            TableCell(String.format("%.4f", measurement.value), 2f)
            TableCell(measurement.status.toString(), 1f)
            Row(Modifier.width(60.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { editing = true }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        painterResource(Res.drawable.edit),
                        null,
                        tint = SensumThemeColors.muted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = { measurement.id?.let { onDelete(it) } }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        painterResource(Res.drawable.trash),
                        null,
                        tint = SensumThemeColors.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InlineField(value: String, modifier: Modifier, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.padding(end = 4.dp),
        textStyle = MaterialTheme.typography.bodySmall.copy(color = SensumThemeColors.onSurface),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = SensumThemeColors.accent,
            unfocusedIndicatorColor = SensumThemeColors.border,
            cursorColor = SensumThemeColors.accent
        )
    )
}

@Composable
private fun RowScope.TableCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.bodySmall,
        color = SensumThemeColors.onSurface
    )
}

@Composable
private fun RowScope.TableHeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.labelMedium,
        color = SensumThemeColors.accent
    )
}