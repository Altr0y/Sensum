package si.sensum.demo.components.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.AppThemeToggle
import si.sensum.demo.model.UiStatus
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.brightness_alert
import si.sensum.demo.resources.logo_simple_vector
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
                .height(SensumSizes.topBarHeight),
            verticalAlignment = Alignment.CenterVertically
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
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_simple_vector),
                    contentDescription = "Toggle sidebar",
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(Modifier.width(22.dp))

            StretchedTopBarTitle(
                text = activeTab.title,
                modifier = Modifier.width(
                    when (activeTab) {
                        SideBarTab.DATA -> 132.dp
                        SideBarTab.HOME -> 142.dp
                        SideBarTab.RECORDS -> 190.dp
                        SideBarTab.MEASUREMENT_CHART -> 178.dp
                        SideBarTab.DIGITAL_TWIN -> 270.dp
                        SideBarTab.USER -> 142.dp
                        SideBarTab.SETTINGS -> 218.dp
                        SideBarTab.INFO -> 132.dp
                    }
                )
            )

            Spacer(Modifier.width(48.dp))

            TopBarStatus(status = status)

            Spacer(Modifier.weight(1f))

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

            Spacer(Modifier.width(SensumSpacing.md))
        }
    }
}

@Composable
private fun StretchedTopBarTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        text.uppercase().forEach { character ->
            Text(
                text = character.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = SensumThemeColors.accent
            )
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
        statusUi.icon?.let { icon ->
            Icon(
                painter = painterResource(icon),
                contentDescription = statusUi.text,
                tint = statusUi.color,
                modifier = Modifier.size(SensumSizes.statusIconSize)
            )
        }

        Text(
            text = statusUi.text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            ),
            color = statusUi.color,
            maxLines = 1
        )
    }
}

private data class StatusUi(
    val text: String,
    val color: androidx.compose.ui.graphics.Color,
    val icon: DrawableResource?
)