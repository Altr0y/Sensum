package si.sensum.demo.screens.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.components.background.ResourceBackground
import si.sensum.demo.components.layout.ScreenContainer
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumStatusText
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.waves_dark_bg_vector
import si.sensum.demo.resources.waves_light_bg_vector

@Composable
fun DataRecordsScreen(
    apiClient: SensumApiClient,
    initialMeasurements: List<MeasurementUi>,
    isDark: Boolean,
    onAddData: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val state = remember(apiClient) {
        DataRecordsState(
            apiClient = apiClient,
            scope = scope
        )
    }

    LaunchedEffect(Unit) {
        state.load()
    }

    ResourceBackground(
        isDark = isDark,
        darkResource = Res.drawable.waves_dark_bg_vector,
        lightResource = Res.drawable.waves_light_bg_vector,
        darkOverlayAlpha = 0.40f,
        lightOverlayAlpha = 0.56f
    ) {
        ScreenContainer(
            scrollable = true,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RecordsHeader(
                state = state,
                onAddData = onAddData
            )

            Text(
                text = "Server-side records view with API filtering, sorting and pagination.",
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.muted
            )

            HorizontalDivider(color = SensumThemeColors.border)

            RecordsQueryBar(state)

            SensumStatusText(status = state.status)

            RecordsTable(state)
        }
    }
}