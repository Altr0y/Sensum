package si.sensum.demo.components.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumThemeColors

@Composable
fun SensumEmptyState(
    icon: DrawableResource,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = SensumThemeColors.border,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = SensumThemeColors.muted
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.border,
                textAlign = TextAlign.Center
            )
        }
    }
}