package si.sensum.demo.components.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumRadius
import si.sensum.demo.components.theme.SensumThemeColors

/** Icon + label toggle button used for source/entity/tab selection rows. */
@Composable
fun SensumTabButton(
    text: String,
    icon: DrawableResource,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(SensumRadius.lg),
        color = if (selected) {
            SensumThemeColors.accentMuted.copy(alpha = 0.34f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
        },
        contentColor = if (selected) SensumThemeColors.accent else SensumThemeColors.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) SensumThemeColors.accent else SensumThemeColors.border
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                tint = if (selected) SensumThemeColors.accent else SensumThemeColors.muted,
                modifier = Modifier.width(18.dp)
            )

            androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) SensumThemeColors.accent else SensumThemeColors.onSurface,
                maxLines = 1
            )
        }
    }
}
