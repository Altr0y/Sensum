package si.sensum.demo.screens.data.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.screens.data.DataScreenState

@Composable
fun DslDataForm(
    state: DataScreenState
) {
    Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sensum DSL source",
                style = MaterialTheme.typography.labelMedium,
                color = SensumThemeColors.muted,
                modifier = Modifier.weight(1f)
            )
//
//            SensumButton(
//                text = "Load .sensum file",
//                onClick = 1,
//                variant = SensumButtonVariant.Outline,
//                compact = true
//            )

            if (state.dslSourceText.isNotBlank() || state.geoJsonText.isNotBlank()) {
                SensumButton(
                    text = "Clear",
                    onClick = {
                        state.dslSourceText = ""
                        state.geoJsonText = ""
                        state.updateSqlPreview()
                    },
                    variant = SensumButtonVariant.Ghost,
                    compact = true
                )
            }
        }

        SensumTextField(
            value = state.dslSourceText,
            onValueChange = {
                state.dslSourceText = it
                state.updateSqlPreview()
            },
            label = "country \"Slovenia\" { ... }",
            singleLine = false,
            modifier = Modifier.heightIn(min = 260.dp)
        )

        if (state.geoJsonText.isNotBlank()) {
            SensumTextField(
                value = state.geoJsonText,
                onValueChange = { state.geoJsonText = it },
                label = "GeoJSON output",
                singleLine = false,
                modifier = Modifier.heightIn(min = 180.dp)
            )
        }
    }
}