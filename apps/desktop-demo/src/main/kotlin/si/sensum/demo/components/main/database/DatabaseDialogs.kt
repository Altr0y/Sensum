package si.sensum.demo.components.main.database

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import si.sensum.demo.components.theme.SensumThemeColors

@Composable
fun DatabaseDialogs(
    showSaveDialog: Boolean,
    showDeleteAllDialog: Boolean,
    loadedMeasurementsCount: Int,
    databaseMeasurementsCount: Int,
    onDismissSave: () -> Unit,
    onDismissDeleteAll: () -> Unit,
    onConfirmSave: () -> Unit,
    onConfirmDeleteAll: () -> Unit
) {
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = onDismissSave,
            title = { Text("Save through API Gateway") },
            text = {
                Text("Save $loadedMeasurementsCount loaded measurements through API Gateway?")
            },
            confirmButton = {
                Button(
                    onClick = onConfirmSave,
                    colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.accent)
                ) {
                    Text("Save", color = SensumThemeColors.onAccent)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissSave) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = onDismissDeleteAll,
            title = { Text("Delete All") },
            text = {
                Text("This will delete all $databaseMeasurementsCount measurements through API Gateway.")
            },
            confirmButton = {
                Button(
                    onClick = onConfirmDeleteAll,
                    colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.error)
                ) {
                    Text("Delete All", color = SensumThemeColors.onAccent)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissDeleteAll) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}