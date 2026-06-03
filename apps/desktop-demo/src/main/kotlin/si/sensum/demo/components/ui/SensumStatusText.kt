package si.sensum.demo.components.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.model.UiStatus

@Composable
fun SensumStatusText(
    status: UiStatus,
    modifier: Modifier = Modifier
) {
    when (status) {
        UiStatus.Idle -> Unit

        UiStatus.Loading -> Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = SensumThemeColors.accent,
                strokeWidth = 2.dp
            )
        }

        is UiStatus.Success -> Text(
            text = status.message,
            modifier = modifier,
            style = MaterialTheme.typography.bodyMedium,
            color = SensumThemeColors.success
        )

        is UiStatus.Error -> Text(
            text = status.message,
            modifier = modifier,
            style = MaterialTheme.typography.bodyMedium,
            color = SensumThemeColors.error
        )
    }
}