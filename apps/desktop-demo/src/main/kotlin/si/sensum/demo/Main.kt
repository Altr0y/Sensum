package si.sensum.demo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.layout.SideBarTab
import si.sensum.demo.components.theme.SensumTheme
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.eltratec_logo

fun main() = application {
    val appStateHolder = remember { SensumAppStateHolder() }
    val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
    val trayState = rememberTrayState()
    val icon = painterResource(Res.drawable.eltratec_logo)

    var isDark by remember { mutableStateOf(true) }
    var isWindowVisible by remember { mutableStateOf(true) }

    Tray(
        icon = icon,
        state = trayState,
        tooltip = "Sensum",
        menu = {
            Item(
                text = if (isWindowVisible) "Hide" else "Show",
                onClick = {
                    isWindowVisible = !isWindowVisible
                }
            )

            if (appStateHolder.uiState.isLoggedIn) {
                Separator()

                SideBarTab.entries.forEach { tab ->
                    Item(
                        text = tab.title,
                        onClick = {
                            appStateHolder.selectTab(tab)
                            isWindowVisible = true
                        }
                    )
                }

                Separator()

                Item(
                    text = "Logout",
                    onClick = {
                        appStateHolder.showLogoutDialog()
                        isWindowVisible = true
                    }
                )
            }

            Separator()

            Item(
                text = "Exit",
                onClick = {
                    exitApplication()
                }
            )
        }
    )

    if (isWindowVisible) {
        Window(
            onCloseRequest = {
                isWindowVisible = false
            },
            title = "Sensum",
            icon = icon,
            state = windowState
        ) {
            SensumTheme(isDark = isDark) {
                App(
                    stateHolder = appStateHolder,
                    isDark = isDark,
                    onToggleTheme = { isDark = !isDark }
                )
            }
        }
    }
}