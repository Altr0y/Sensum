package si.sensum.demo.components.main.database

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.model.UiStatus

@Composable
fun DatabaseToolbar(
    hasLoadedMeasurements: Boolean,
    hasDatabaseMeasurements: Boolean,
    status: UiStatus,
    onSaveLoadedData: () -> Unit,
    onRefresh: () -> Unit,
    onDeleteAll: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onSaveLoadedData,
            enabled = hasLoadedMeasurements,
            colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.accent)
        ) {
            Text("Save Loaded Data", color = SensumThemeColors.onAccent)
        }

        OutlinedButton(onClick = onRefresh) {
            Text("Refresh", color = SensumThemeColors.muted)
        }

        OutlinedButton(
            onClick = onDeleteAll,
            enabled = hasDatabaseMeasurements
        ) {
            Text("Delete All", color = SensumThemeColors.error)
        }

        when (status) {
            UiStatus.Loading -> CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = SensumThemeColors.accent,
                strokeWidth = 2.dp
            )

            is UiStatus.Success -> Text(
                text = status.message,
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.success
            )

            is UiStatus.Error -> Text(
                text = status.message,
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.error
            )

            UiStatus.Idle -> Unit
        }
    }
}