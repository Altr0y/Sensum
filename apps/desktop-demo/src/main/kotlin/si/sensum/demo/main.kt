package si.sensum.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import si.sensum.demo.components.SensumTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sensum",
        state = rememberWindowState(
            placement = WindowPlacement.Maximized
        )
    ) {
        SensumTheme {
            App()
        }
    }
}