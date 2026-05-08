package si.sensum.demo

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import si.sensum.demo.components.*
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import si.sensum.demo.model.Measurement
@Composable
fun App() {
    var sidebarExpanded by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(SideBarTab.DATA_LOADER) }
    var measurements by remember { mutableStateOf<List<Measurement>>(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {
        TitleBar(
            sidebarExpanded = sidebarExpanded,
            onToggleSidebar = { sidebarExpanded = !sidebarExpanded }
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
                    SideBarTab.MEASUREMENT_CHART -> MeasurementChart(measurements = measurements)
                    SideBarTab.USER -> User()
                    SideBarTab.INFO -> Info()
                }
            }
        }
    }
}