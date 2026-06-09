package si.sensum.demo.screens.data.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.model.UiStatus
import si.sensum.demo.screens.data.model.DataSourceType

@Composable
fun DataActionButtons(
    sourceType: DataSourceType,
    status: UiStatus,
    previewVisible: Boolean,
    onRun: () -> Unit,
    onOpenRecords: () -> Unit,
    onOpenOnline: () -> Unit,
    onTogglePreview: () -> Unit
) {
    val isLoading = status is UiStatus.Loading

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SensumButton(
            text = when (sourceType) {
                DataSourceType.SWS -> "Refresh"
                DataSourceType.DSL -> "Process DSL"
                DataSourceType.SIM -> "Generate"
                DataSourceType.MANUAL -> "Add"
            },
            onClick = onRun,
            enabled = !isLoading,
            isLoading = isLoading
        )

        SensumButton(
            text = "Open records",
            onClick = onOpenRecords,
            enabled = !isLoading,
            variant = SensumButtonVariant.Outline
        )

        if (sourceType == DataSourceType.SIM) {
            SensumButton(
                text = "View online",
                onClick = onOpenOnline,
                enabled = !isLoading,
                variant = SensumButtonVariant.Outline
            )
        }

        SensumButton(
            text = if (previewVisible) "Hide preview" else "Show preview",
            onClick = onTogglePreview,
            enabled = !isLoading,
            variant = SensumButtonVariant.Secondary
        )
    }
}