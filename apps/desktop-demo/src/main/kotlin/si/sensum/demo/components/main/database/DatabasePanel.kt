package si.sensum.demo.components.main.database

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.components.EmptyState
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.database_panel

@Composable
fun DatabasePanel(
    measurements: List<MeasurementUi>,
    apiClient: SensumApiClient
) {
    val scope = rememberCoroutineScope()
    val state = remember(apiClient) {
        DatabasePanelState(apiClient, scope)
    }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        state.load()
    }

    val grouped = state.measurements.groupBy { it.channelId }.toSortedMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Database Panel", style = MaterialTheme.typography.titleLarge)

        Text(
            "${state.measurements.size} measurements across ${grouped.size} channels",
            style = MaterialTheme.typography.bodyMedium,
            color = SensumThemeColors.muted
        )

        HorizontalDivider(color = SensumThemeColors.border)

        DatabaseToolbar(
            hasLoadedMeasurements = measurements.isNotEmpty(),
            hasDatabaseMeasurements = state.measurements.isNotEmpty(),
            status = state.status,
            onSaveLoadedData = { showSaveDialog = true },
            onRefresh = { state.load() },
            onDeleteAll = { showDeleteAllDialog = true }
        )

        HorizontalDivider(color = SensumThemeColors.border)

        if (state.measurements.isEmpty() && !state.isLoading) {
            EmptyState(
                icon = Res.drawable.database_panel,
                title = "No data in database",
                subtitle = "Load measurements in Data Loader\nand press Save Loaded Data."
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grouped.forEach { (channelId, entries) ->
                    MeasurementChannelAccordion(
                        channelId = channelId,
                        entries = entries,
                        onUpdate = state::update,
                        onDelete = state::delete
                    )
                }
            }
        }
    }

    DatabaseDialogs(
        showSaveDialog = showSaveDialog,
        showDeleteAllDialog = showDeleteAllDialog,
        loadedMeasurementsCount = measurements.size,
        databaseMeasurementsCount = state.measurements.size,
        onDismissSave = { showSaveDialog = false },
        onDismissDeleteAll = { showDeleteAllDialog = false },
        onConfirmSave = {
            showSaveDialog = false
            state.saveLoadedData(measurements)
        },
        onConfirmDeleteAll = {
            showDeleteAllDialog = false
            state.deleteAll()
        }
    )
}