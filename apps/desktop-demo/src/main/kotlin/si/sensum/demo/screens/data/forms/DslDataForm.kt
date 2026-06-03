package si.sensum.demo.screens.data.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.screens.data.DataScreenState

@Composable
fun DslDataForm(
    state: DataScreenState
) {
    Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
        SensumTextField(
            value = state.dslSourceText,
            onValueChange = {
                state.dslSourceText = it
                state.updateSqlPreview()
            },
            label = "Sensum DSL",
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
