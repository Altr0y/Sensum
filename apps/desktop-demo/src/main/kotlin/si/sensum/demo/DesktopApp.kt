package si.sensum.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.components.layout.AppScaffold
import si.sensum.demo.components.layout.SideBarTab
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumDialog
import si.sensum.demo.model.UiStatus
import si.sensum.demo.screens.records.DataRecordsScreen
import si.sensum.demo.screens.auth.LoginScreen
import si.sensum.demo.screens.charts.MeasurementChart
import si.sensum.demo.screens.data.DataScreen
import si.sensum.demo.screens.data.DigitalTwin
import si.sensum.demo.screens.home.HomeScreen
import si.sensum.demo.screens.info.Info
import si.sensum.demo.screens.info.User
import si.sensum.demo.screens.settings.SettingsScreen

@Composable
fun App(
    stateHolder: SensumAppStateHolder,
    isDark: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {
    val apiClient = remember { SensumApiClient() }
    val scope = rememberCoroutineScope()
    val uiState = stateHolder.uiState

    if (!uiState.isLoggedIn) {
        LoginScreen(
            isDark = isDark,
            onToggleTheme = onToggleTheme,
            onLogin = { username, password ->
                apiClient.login(username, password)
            },
            onLoginSuccess = {
                stateHolder.loginSucceeded(
                    isAdmin = false
                )
            }
        )
        return
    }

    AppScaffold(
        sidebarExpanded = uiState.sidebarExpanded,
        activeTab = uiState.activeTab,
        isDark = isDark,
        isAdmin = uiState.isAdmin,
        status = uiState.appStatus,
        onToggleSidebar = stateHolder::toggleSidebar,
        onToggleTheme = onToggleTheme,
        onTabSelected = stateHolder::selectTab,
        onRefreshClick = stateHolder::showRefreshDialog,
        onLogoutClick = stateHolder::showLogoutDialog
    ) {
        when (uiState.activeTab) {
            SideBarTab.HOME -> HomeScreen(
                isDark = isDark,
                onNavigate = stateHolder::selectTab
            )

            SideBarTab.DATA -> DataScreen(
                apiClient = apiClient,
                isDark = isDark,
                onStatusChange = stateHolder::setStatus,
                onOpenRecords = { loadedMeasurements ->
                    stateHolder.setMeasurements(loadedMeasurements)
                    stateHolder.selectTab(SideBarTab.RECORDS)
                }
            )

            SideBarTab.RECORDS -> DataRecordsScreen(
                apiClient = apiClient,
                initialMeasurements = uiState.measurements,
                isDark = isDark,
                onAddData = {
                    stateHolder.selectTab(SideBarTab.DATA)
                }
            )

            SideBarTab.MEASUREMENT_CHART -> MeasurementChart(apiClient = apiClient)

            SideBarTab.DIGITAL_TWIN -> DigitalTwin(apiClient = apiClient)

            SideBarTab.USER -> User()

            SideBarTab.SETTINGS -> SettingsScreen()

            SideBarTab.INFO -> Info()
        }
    }

    if (uiState.showRefreshDialog) {
        SensumDialog(
            title = "Refresh state",
            message = "Refresh current data and connection state?",
            confirmText = "Refresh",
            dismissText = "Cancel",
            onDismiss = stateHolder::hideRefreshDialog,
            onConfirm = {
                stateHolder.hideRefreshDialog()

                scope.launch {
                    stateHolder.setStatus(UiStatus.Loading)

                    runCatching {
                        apiClient.getMeasurements()
                    }.fold(
                        onSuccess = { measurements ->
                            stateHolder.setMeasurements(measurements)
                            stateHolder.setStatus(UiStatus.Success("State refreshed"))
                        },
                        onFailure = { error ->
                            stateHolder.setStatus(
                                UiStatus.Error(error.message ?: "Refresh failed")
                            )
                        }
                    )
                }
            }
        )
    }

    if (uiState.showLogoutDialog) {
        SensumDialog(
            title = "Logout",
            message = "End current session and return to login?",
            confirmText = "Logout",
            dismissText = "Cancel",
            confirmVariant = SensumButtonVariant.Danger,
            onDismiss = stateHolder::hideLogoutDialog,
            onConfirm = {
                stateHolder.hideLogoutDialog()

                scope.launch {
                    stateHolder.setStatus(UiStatus.Loading)
                    runCatching { apiClient.logout() }
                    stateHolder.logoutLocal()
                }
            }
        )
    }
}