package si.sensum.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.components.*
import si.sensum.demo.components.main.*
import si.sensum.demo.components.main.database.DatabasePanel
import si.sensum.demo.model.MeasurementUi

@Composable
fun App(
    isDark: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {
    val apiClient = remember { SensumApiClient() }

    var isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        LoginScreen(
            onLogin = { username, password ->
                apiClient.login(username, password)
            },
            onLoginSuccess = {
                isLoggedIn = true
            }
        )
        return
    }

    var sidebarExpanded by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(SideBarTab.DATA_LOADER) }
    var measurements by remember { mutableStateOf<List<MeasurementUi>>(emptyList()) }

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
                    SideBarTab.DATA_LOADER -> DataLoader(
                        apiClient = apiClient,
                        onMeasurementsLoaded = { measurements = it }
                    )

                    SideBarTab.DATABASE_PANEL -> DatabasePanel(
                        measurements = measurements,
                        apiClient = apiClient
                    )

                    SideBarTab.MEASUREMENT_CHART -> MeasurementChart(
                        apiClient = apiClient
                    )

                    SideBarTab.DIGITAL_TWIN -> DigitalTwin(
                        apiClient = apiClient
                    )

                    SideBarTab.USER -> User()
                    SideBarTab.INFO -> Info()
                }
            }
        }
    }
}