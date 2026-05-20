package si.sensum.demo

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import si.sensum.demo.components.theme.SensumTheme
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.eltratec_logo

fun main() = application {
    var isDark by remember { mutableStateOf(true) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Sensum",
        icon = painterResource(Res.drawable.eltratec_logo),
        state = rememberWindowState(placement = WindowPlacement.Maximized)
    ) {
        SensumTheme(isDark = isDark) {
            App(
                isDark = isDark,
                onToggleTheme = { isDark = !isDark }
            )
        }
    }
}