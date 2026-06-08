package si.sensum.demo.screens.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumTabButton

@Composable
fun RecordsHeader(
    state: DataRecordsState,
    onAddData: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
        ) {

        }

        SensumButton(
            text = "+ Add data",
            onClick = onAddData,
            variant = SensumButtonVariant.Outline,
            compact = true
        )
    }
}
