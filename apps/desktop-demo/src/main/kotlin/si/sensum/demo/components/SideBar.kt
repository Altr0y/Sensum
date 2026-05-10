package si.sensum.demo.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.chart
import si.sensum.demo.resources.data_load
import si.sensum.demo.resources.database_panel
import si.sensum.demo.resources.info
import si.sensum.demo.resources.user

enum class SideBarTab { DATA_LOADER, DATABASE_PANEL, MEASUREMENT_CHART, USER, INFO }

@Composable
fun SideBar(
    expanded: Boolean,
    activeTab: SideBarTab,
    onTabSelected: (SideBarTab) -> Unit
) {
    val width by animateDpAsState(targetValue = if (expanded) 220.dp else 64.dp)

    Surface(
        modifier = Modifier.width(width).fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.weight(1f).padding(vertical = 10.dp)) {
                NavItem(
                    icon = Res.drawable.data_load,
                    label = "Data Loader",
                    active = activeTab == SideBarTab.DATA_LOADER,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.DATA_LOADER) }
                )

                NavItem(
                    icon = Res.drawable.database_panel,
                    label = "Database Panel",
                    active = activeTab == SideBarTab.DATABASE_PANEL,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.DATABASE_PANEL) }
                )

                NavItem(
                    icon = Res.drawable.chart,
                    label = "Measurement Chart",
                    active = activeTab == SideBarTab.MEASUREMENT_CHART,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.MEASUREMENT_CHART) }
                )
            }

            HorizontalDivider(color = SensumColors.Border, thickness = 2.dp)

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                NavItem(
                    icon = Res.drawable.user,
                    label = "Profile",
                    active = activeTab == SideBarTab.USER,
                    expanded = expanded,
                    onClick = { onTabSelected(SideBarTab.USER) }
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
    icon: DrawableResource,
    label: String,
    active: Boolean,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (active) MaterialTheme.colorScheme.surfaceVariant
    else MaterialTheme.colorScheme.surface

    val iconTint = if (active) SensumColors.Accent else SensumColors.Muted

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(bgColor)
            .clickable { onClick() }
    ) {
        if (active) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .padding(vertical = 6.dp)
                    .background(SensumColors.Accent)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(35.dp)
            )

            if (expanded) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (active) SensumColors.OnSurface else SensumColors.Muted
                )
            }
        }
    }
}