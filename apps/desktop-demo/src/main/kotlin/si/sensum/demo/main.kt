package si.sensum.demo

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sensum Desktop App",
        state = rememberWindowState(
            placement = WindowPlacement.Maximized
        )
    ) {
        App()
    }
}