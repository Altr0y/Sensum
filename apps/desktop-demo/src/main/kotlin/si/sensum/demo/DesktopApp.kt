package si.sensum.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import si.sensum.demo.components.*
import si.sensum.demo.components.main.*
import si.sensum.demo.model.Measurement

@Composable
fun App(
    isDark: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {
    var sidebarExpanded by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(SideBarTab.DATA_LOADER) }
    var measurements by remember { mutableStateOf<List<Measurement>>(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {
        TitleBar(
            sidebarExpanded = sidebarExpanded,
            onToggleSidebar = { sidebarExpanded = !sidebarExpanded },
            isDark = isDark,
            onToggleTheme = onToggleTheme
        )
        Row(modifier = Modifier.fillMaxSize()) {
            SideBar(
                expanded = sidebarExpanded,
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (activeTab) {
                    SideBarTab.DATA_LOADER -> DataLoader(onMeasurementsLoaded = { measurements = it })
                    SideBarTab.DATABASE_PANEL -> DatabasePanel(measurements = measurements)
                    SideBarTab.MEASUREMENT_CHART -> MeasurementChart()
                    SideBarTab.DIGITAL_TWIN -> DigitalTwin()
                    SideBarTab.USER -> User()
                    SideBarTab.INFO -> Info()
                }
            }
        }
    }
}
