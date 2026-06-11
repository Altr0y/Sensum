package si.sensum.demo.components.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors

@Composable
fun SensumCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(SensumSpacing.lg),
    fillMaxWidth: Boolean = true,
    overlay: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = if (fillMaxWidth) modifier.fillMaxWidth() else modifier,
        color = if (overlay) SensumThemeColors.surfaceOverlay else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, SensumThemeColors.border)
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
