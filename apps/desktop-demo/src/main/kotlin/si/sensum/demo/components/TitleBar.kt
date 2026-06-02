package si.sensum.demo.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.*


@Composable
fun TitleBar(
    sidebarExpanded: Boolean,
    onToggleSidebar: () -> Unit,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onToggleSidebar) {
                Icon(
                    painter = painterResource(
                        if (sidebarExpanded) Res.drawable.sidebar_collapse
                        else Res.drawable.sidebar_expand
                    ),
                    contentDescription = if (sidebarExpanded) "Collapse sidebar" else "Expand sidebar",
                    tint = SensumThemeColors.muted,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = onToggleTheme) {
                Icon(
                    painter = painterResource(
                        if (isDark) Res.drawable.sun else Res.drawable.moon
                    ),
                    contentDescription = "Toggle theme",
                    tint = SensumThemeColors.muted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Image(
                painter = painterResource(Res.drawable.eltratec_logo),
                contentDescription = "Eltratec logo",
                modifier = Modifier.height(42.dp)
            )
        }
    }
}
