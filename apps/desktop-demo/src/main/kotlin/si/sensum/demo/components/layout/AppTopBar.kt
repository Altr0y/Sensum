package si.sensum.demo.components.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.AppThemeToggle
import si.sensum.demo.model.UiStatus
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.brightness_alert
import si.sensum.demo.resources.logo_transparent
import si.sensum.demo.resources.verified

@Composable
fun AppTopBar(
    sidebarExpanded: Boolean,
    activeTab: SideBarTab,
    isDark: Boolean,
    status: UiStatus,
    onToggleSidebar: () -> Unit,
    onToggleTheme: () -> Unit,
    onRefreshClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(SensumSizes.topBarHeight)
                .padding(horizontal = SensumSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(
                        if (sidebarExpanded) {
                            SensumSizes.topBarSideExpandedWidth
                        } else {
                            SensumSizes.topBarSideCollapsedWidth
                        }
                    )
                    .clickable { onToggleSidebar() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_transparent),
                    contentDescription = "Toggle sidebar",
                    modifier = Modifier.height(SensumSizes.topBarLogoHeight)
                )

                Text(
                    text = if (sidebarExpanded) "<" else ">",
                    style = MaterialTheme.typography.titleMedium,
                    color = SensumThemeColors.muted
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeTab.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = SensumThemeColors.onSurface
                )
            }

            TopBarStatus(status = status)

            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh state",
                    tint = SensumThemeColors.muted,
                    modifier = Modifier.size(SensumSizes.topBarIconSize)
                )
            }

            AppThemeToggle(
                isDark = isDark,
                onToggleTheme = onToggleTheme
            )

            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = SensumThemeColors.error,
                    modifier = Modifier.size(SensumSizes.topBarIconSize)
                )
            }
        }
    }
}

@Composable
private fun TopBarStatus(status: UiStatus) {
    val statusUi = when (status) {
        UiStatus.Idle -> StatusUi(
            text = "Ready",
            color = SensumThemeColors.muted,
            icon = null
        )

        UiStatus.Loading -> StatusUi(
            text = "Loading",
            color = SensumThemeColors.warning,
            icon = null
        )

        is UiStatus.Success -> StatusUi(
            text = status.message,
            color = SensumThemeColors.success,
            icon = Res.drawable.verified
        )

        is UiStatus.Error -> StatusUi(
            text = status.message,
            color = SensumThemeColors.error,
            icon = Res.drawable.brightness_alert
        )
    }

    Row(
        modifier = Modifier.width(SensumSizes.topBarStatusWidth),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
    ) {
        if (statusUi.icon != null) {
            Icon(
                painter = painterResource(statusUi.icon),
                contentDescription = null,
                tint = statusUi.color,
                modifier = Modifier.size(SensumSizes.statusIconSize)
            )
        }

        Text(
            text = statusUi.text,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
            color = statusUi.color
        )
    }
}

private data class StatusUi(
    val text: String,
    val color: androidx.compose.ui.graphics.Color,
    val icon: org.jetbrains.compose.resources.DrawableResource?
)