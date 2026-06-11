package si.sensum.demo.components.layout

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.chart
import si.sensum.demo.resources.data_load
import si.sensum.demo.resources.database_panel
import si.sensum.demo.resources.digital_twin
import si.sensum.demo.resources.info
import si.sensum.demo.resources.person_shield
import si.sensum.demo.resources.settings_ethernet

enum class SideBarTab(
    val title: String
) {
    HOME("Home"),
    DATA("Data"),
    RECORDS("Records"),
    MEASUREMENT_CHART("Charts"),
    DIGITAL_TWIN("Digital Twin"),
    USER("Users"),
    SETTINGS("Settings"),
    INFO("Info")
}

@Composable
fun AppSideBar(
    expanded: Boolean,
    activeTab: SideBarTab,
    isAdmin: Boolean,
    onTabSelected: (SideBarTab) -> Unit
) {
    val width by animateDpAsState(
        targetValue = if (expanded) {
            SensumSizes.sideBarExpanded
        } else {
            SensumSizes.sideBarCollapsed
        }
    )

    Surface(
        modifier = Modifier
            .width(width)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = SensumSpacing.sm)
            ) {
                NavItem(
                    vectorIcon = Icons.Filled.Home,
                    label = "Home",
                    active = activeTab == SideBarTab.HOME,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.HOME) }
                )

                NavItem(
                    icon = Res.drawable.data_load,
                    label = "Data",
                    active = activeTab == SideBarTab.DATA,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.DATA) }
                )

                NavItem(
                    icon = Res.drawable.database_panel,
                    label = "Records",
                    active = activeTab == SideBarTab.RECORDS,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.RECORDS) }
                )

                NavItem(
                    icon = Res.drawable.chart,
                    label = "Charts",
                    active = activeTab == SideBarTab.MEASUREMENT_CHART,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.MEASUREMENT_CHART) }
                )

                NavItem(
                    icon = Res.drawable.digital_twin,
                    label = "Digital Twin",
                    active = activeTab == SideBarTab.DIGITAL_TWIN,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.DIGITAL_TWIN) }
                )
            }

            HorizontalDivider(color = SensumThemeColors.border, thickness = SensumSizes.fieldBorderWidth)

            Column(modifier = Modifier.padding(vertical = SensumSpacing.sm)) {
                NavItem(
                    icon = Res.drawable.person_shield,
                    label = if (isAdmin) "Users" else "Password",
                    active = activeTab == SideBarTab.USER,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.USER) }
                )

                NavItem(
                    icon = Res.drawable.settings_ethernet,
                    label = "Settings",
                    active = activeTab == SideBarTab.SETTINGS,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.SETTINGS) }
                )

                NavItem(
                    icon = Res.drawable.info,
                    label = "Info",
                    active = activeTab == SideBarTab.INFO,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.INFO) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: DrawableResource? = null,
    vectorIcon: ImageVector? = null,
    label: String,
    active: Boolean,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (active) {
        SensumThemeColors.accentMuted.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val iconTint = if (active) {
        SensumThemeColors.accent
    } else {
        SensumThemeColors.muted
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SensumSizes.sideBarItemHeight)
            .background(bgColor)
            .clickable { onClick() }
    ) {
        if (active) {
            Box(
                modifier = Modifier
                    .width(SensumSizes.sideBarActiveIndicatorWidth)
                    .fillMaxHeight()
                    .background(SensumThemeColors.accent)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SensumSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md)
        ) {
            when {
                icon != null -> Icon(
                    painter = painterResource(icon),
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(SensumSizes.sideBarIconSize)
                )

                vectorIcon != null -> Icon(
                    imageVector = vectorIcon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(SensumSizes.sideBarIconSize)
                )
            }

            if (expanded) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (active) {
                        SensumThemeColors.onSurface
                    } else {
                        SensumThemeColors.muted
                    }
                )
            }
        }
    }
}