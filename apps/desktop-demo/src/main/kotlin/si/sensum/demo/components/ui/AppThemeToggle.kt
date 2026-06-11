package si.sensum.demo.components.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.moon
import si.sensum.demo.resources.sun

@Composable
fun AppThemeToggle(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggleTheme,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(
                if (isDark) {
                    Res.drawable.sun
                } else {
                    Res.drawable.moon
                }
            ),
            contentDescription = "Toggle theme",
            tint = SensumThemeColors.muted,
            modifier = Modifier.size(SensumSizes.themeToggleIconSize)
        )
    }
}