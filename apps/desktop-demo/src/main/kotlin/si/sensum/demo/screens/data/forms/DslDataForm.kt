package si.sensum.demo.screens.data.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.screens.data.DataScreenState

@Composable
fun DslDataForm(
    state: DataScreenState
) {
    SensumCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)
        ) {
            SensumTextField(
                value = state.dslSourceText,
                onValueChange = {
                    state.dslSourceText = it
                    state.updateSqlPreview()
                },
                label = "DSL source",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 260.dp,
                        max = 520.dp
                    ),
                singleLine = false
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
            ) {
                SensumButton(
                    text = "Import .sensum / .txt",
                    onClick = state::importDslFile,
                    variant = SensumButtonVariant.Outline,
                    compact = true
                )

                SensumButton(
                    text = "Process DSL",
                    onClick = state::runPrimaryAction,
                    variant = SensumButtonVariant.Primary,
                    compact = true
                )
            }
        }
    }
}