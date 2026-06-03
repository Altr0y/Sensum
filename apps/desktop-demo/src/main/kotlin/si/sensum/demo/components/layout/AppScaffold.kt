package si.sensum.demo.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import si.sensum.demo.model.UiStatus

@Composable
fun AppScaffold(
    sidebarExpanded: Boolean,
    activeTab: SideBarTab,
    isDark: Boolean,
    isAdmin: Boolean,
    status: UiStatus,
    onToggleSidebar: () -> Unit,
    onToggleTheme: () -> Unit,
    onTabSelected: (SideBarTab) -> Unit,
    onRefreshClick: () -> Unit,
    onLogoutClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            sidebarExpanded = sidebarExpanded,
            activeTab = activeTab,
            isDark = isDark,
            status = status,
            onToggleSidebar = onToggleSidebar,
            onToggleTheme = onToggleTheme,
            onRefreshClick = onRefreshClick,
            onLogoutClick = onLogoutClick
        )

        Row(modifier = Modifier.fillMaxSize()) {
            AppSideBar(
                expanded = sidebarExpanded,
                activeTab = activeTab,
                isAdmin = isAdmin,
                onTabSelected = onTabSelected
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                content()
            }
        }
    }
}