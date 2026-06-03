package si.sensum.demo.screens.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumButton

@Composable
fun RecordsHeader(
    state: DataRecordsState,
    onAddData: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Records",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        RecordsEntityType.entries.forEach { type ->
            FilterChip(
                selected = state.entityType == type,
                onClick = { state.changeEntityType(type) },
                label = { Text(type.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SensumThemeColors.accentMuted,
                    selectedLabelColor = SensumThemeColors.accent,
                    labelColor = SensumThemeColors.onSurface
                )
            )
        }

        SensumButton(
            text = "+ Add data",
            onClick = onAddData
        )
    }
}