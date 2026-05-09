package si.sensum.demo.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun TitleBar(
    sidebarExpanded: Boolean,
    onToggleSidebar: () -> Unit
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
                        if (sidebarExpanded) "images/sidebar-collapse.svg"
                        else "images/sidebar-expand.svg"
                    ),
                    contentDescription = if (sidebarExpanded) "Collapse sidebar" else "Expand sidebar",
                    tint = SensumColors.Muted,
                    modifier = Modifier.size(40.dp)
                )
            }

            Image(
                painter = painterResource("images/EltratecLogo.png"),
                contentDescription = "Eltratec logo",
                modifier = Modifier.height(42.dp)
            )
        }
    }
}