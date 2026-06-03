package si.sensum.demo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import si.sensum.demo.components.layout.SideBarTab
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.model.UiStatus

data class SensumAppUiState(
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false,
    val sidebarExpanded: Boolean = false,
    val activeTab: SideBarTab = SideBarTab.HOME,
    val measurements: List<MeasurementUi> = emptyList(),
    val appStatus: UiStatus = UiStatus.Idle,
    val showRefreshDialog: Boolean = false,
    val showLogoutDialog: Boolean = false
)

class SensumAppStateHolder {
    var uiState by mutableStateOf(SensumAppUiState())
        private set

    fun toggleSidebar() {
        uiState = uiState.copy(
            sidebarExpanded = !uiState.sidebarExpanded
        )
    }

    fun selectTab(tab: SideBarTab) {
        uiState = uiState.copy(
            activeTab = tab
        )
    }

    fun setStatus(status: UiStatus) {
        uiState = uiState.copy(
            appStatus = status
        )
    }

    fun setMeasurements(measurements: List<MeasurementUi>) {
        uiState = uiState.copy(
            measurements = measurements
        )
    }

    fun loginSucceeded(isAdmin: Boolean = false) {
        uiState = uiState.copy(
            isLoggedIn = true,
            isAdmin = isAdmin,
            activeTab = SideBarTab.HOME,
            appStatus = UiStatus.Success("Logged in")
        )
    }

    fun logoutLocal() {
        uiState = SensumAppUiState()
    }

    fun showRefreshDialog() {
        uiState = uiState.copy(showRefreshDialog = true)
    }

    fun hideRefreshDialog() {
        uiState = uiState.copy(showRefreshDialog = false)
    }

    fun showLogoutDialog() {
        uiState = uiState.copy(showLogoutDialog = true)
    }

    fun hideLogoutDialog() {
        uiState = uiState.copy(showLogoutDialog = false)
    }
}