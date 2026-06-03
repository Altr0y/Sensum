package si.sensum.demo.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import si.sensum.demo.components.layout.ScreenContainer
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumCard

@Composable
fun SettingsScreen() {
    ScreenContainer(
        scrollable = true,
        contentPadding = PaddingValues(
            horizontal = SensumSizes.screenPaddingHorizontal,
            vertical = SensumSizes.screenPaddingVertical
        ),
        verticalArrangement = Arrangement.spacedBy(SensumSpacing.lg)
    ) {
        SensumCard(
            modifier = Modifier.widthIn(max = SensumSizes.settingsPlaceholderMaxWidth)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                color = SensumThemeColors.onSurface
            )

            Text(
                text = "Tu bodo settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.muted
            )
        }
    }
}